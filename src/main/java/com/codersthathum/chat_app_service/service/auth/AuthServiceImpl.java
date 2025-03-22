package com.codersthathum.chat_app_service.service.auth;

import com.codersthathum.chat_app_service.dto.auth.LoginRequest;
import com.codersthathum.chat_app_service.dto.auth.LoginResponse;
import com.codersthathum.chat_app_service.dto.auth.RefreshTokenRequest;
import com.codersthathum.chat_app_service.dto.auth.RegisterRequest;
import com.codersthathum.chat_app_service.dto.user.UserParam;
import com.codersthathum.chat_app_service.entity.User;
import com.codersthathum.chat_app_service.exception.DuplicateResourceException;
import com.codersthathum.chat_app_service.exception.ResourceNotFoundException;
import com.codersthathum.chat_app_service.exception.UnauthorizedException;
import com.codersthathum.chat_app_service.repository.user.UserRepository;
import com.codersthathum.chat_app_service.repository.user.UserSpecification;
import com.codersthathum.chat_app_service.util.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JWT jwt;

    public void register(RegisterRequest param) {
        try {
            this.userRepository.save(User
                    .builder()
                    .name(param.getName())
                    .email(param.getEmail())
                    .password(this.passwordEncoder.encode(param.getPassword()))
                    .build()
            );
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("email is already exist");
        }
    }

    public LoginResponse login(LoginRequest param) {
        Specification<User> userParam = UserSpecification.param(
                UserParam.builder()
                        .email(param.getEmail())
                        .isActive(true)
                        .build()
        );

        return userRepository.findOne(userParam)
                .filter(user -> passwordEncoder.matches(param.getPassword(), user.getPassword()))
                .map(user -> {
                    user.setRefreshToken(this.jwt.generateRefreshToken(user.getId()));
                    userRepository.save(user);
                    return user;
                })
                .map(user -> LoginResponse.builder()
                        .accessToken(this.jwt.generateAccessToken(user.getId()))
                        .refreshToken(user.getRefreshToken())
                        .build())
                .orElseThrow(() -> {
                    // Determine which exception to throw based on whether user exists
                    return userRepository.exists(userParam)
                            ? new UnauthorizedException("invalid password")
                            : new ResourceNotFoundException("user not found");
                });
    }

    public LoginResponse refreshToken(RefreshTokenRequest param) {
        this.jwt.validateToken(param.getRefreshToken(), JWT.REFRESH_TOKEN_TYPE);

        return this.userRepository.findOne(
                        UserSpecification.param(UserParam.builder()
                                .id(this.jwt.getUserIdFromToken(param.getRefreshToken()))
                                .refreshToken(param.getRefreshToken())
                                .isActive(true)
                                .build()
                        )
                ).map(user -> {
                    user.setRefreshToken(this.jwt.generateRefreshToken(user.getId()));
                    userRepository.save(user);
                    return user;
                })
                .map(user -> LoginResponse.builder()
                        .accessToken(this.jwt.generateAccessToken(user.getId()))
                        .refreshToken(user.getRefreshToken())
                        .build()
                )
                .orElseThrow(() -> new UnauthorizedException("invalid refresh token"));
    }

}
