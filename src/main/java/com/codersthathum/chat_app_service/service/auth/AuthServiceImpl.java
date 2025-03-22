package com.codersthathum.chat_app_service.service.auth;

import com.codersthathum.chat_app_service.dto.auth.LoginRequest;
import com.codersthathum.chat_app_service.dto.auth.LoginResponse;
import com.codersthathum.chat_app_service.dto.auth.RegisterRequest;
import com.codersthathum.chat_app_service.entity.User;
import com.codersthathum.chat_app_service.exception.DuplicateResourceException;
import com.codersthathum.chat_app_service.exception.ResourceNotFoundException;
import com.codersthathum.chat_app_service.exception.UnauthorizedException;
import com.codersthathum.chat_app_service.repository.user.UserRepository;
import com.codersthathum.chat_app_service.util.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        return userRepository.findByEmail(param.getEmail())
                .filter(user -> passwordEncoder.matches(param.getPassword(), user.getPassword()))
                .map(user -> LoginResponse.builder().accessToken(this.jwt.generateToken(user.getId())).build())
                .orElseThrow(() -> {
                    // Determine which exception to throw based on whether user exists
                    return userRepository.existsByEmail(param.getEmail())
                            ? new UnauthorizedException("invalid password")
                            : new ResourceNotFoundException("user not found");
                });
    }

}
