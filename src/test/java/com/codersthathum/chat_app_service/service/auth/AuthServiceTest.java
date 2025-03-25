package com.codersthathum.chat_app_service.service.auth;

import com.codersthathum.chat_app_service.dto.auth.LoginRequest;
import com.codersthathum.chat_app_service.dto.auth.LoginResponse;
import com.codersthathum.chat_app_service.dto.auth.RefreshTokenRequest;
import com.codersthathum.chat_app_service.dto.auth.RegisterRequest;
import com.codersthathum.chat_app_service.entity.User;
import com.codersthathum.chat_app_service.exception.DuplicateResourceException;
import com.codersthathum.chat_app_service.exception.ResourceNotFoundException;
import com.codersthathum.chat_app_service.exception.UnauthorizedException;
import com.codersthathum.chat_app_service.repository.user.UserRepository;
import com.codersthathum.chat_app_service.util.jwt.JWT;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWT jwt;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_WhenValidCredentials_ShouldReturnLoginResponse() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("hashedPassword")
                .isActive(true)
                .build();

        when(userRepository.findOne(any(Specification.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwt.generateAccessToken(user.getId())).thenReturn("access-token");
        when(jwt.generateRefreshToken(user.getId())).thenReturn("refresh-token");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void login_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        // Arrange
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password123");
        when(userRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());
        when(userRepository.exists(any(Specification.class))).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authService.login(request)
        );
        assertEquals("user not found", exception.getMessage());
    }

    @Test
    void login_WhenInvalidPassword_ShouldThrowUnauthorizedException() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("hashedPassword")
                .isActive(true)
                .build();

        when(userRepository.findOne(any(Specification.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);
        when(userRepository.exists(any(Specification.class))).thenReturn(true);

        // Act & Assert
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authService.login(request)
        );
        assertEquals("invalid password", exception.getMessage());
    }

    @Test
    void login_WhenInactiveUser_ShouldReturnEmpty() {
        // Arrange
        LoginRequest request = new LoginRequest("inactive@example.com", "password123");
        User user = User.builder()
                .id(1L)
                .email("inactive@example.com")
                .password("hashedPassword")
                .isActive(false)
                .build();

        when(userRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());
        when(userRepository.exists(any(Specification.class))).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> authService.login(request)
        );
        assertEquals("user not found", exception.getMessage());
    }

    @Test
    void register_WhenValidRequest_ShouldSaveUser() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password123");
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password("hashedPassword")
                .build();

        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        authService.register(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldThrowDuplicateResourceException() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password123");

        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException(""));

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> authService.register(request)
        );

        assertEquals("email is already exist", exception.getMessage());
    }

    @Test
    void refreshToken_WhenValidRefreshToken_ShouldReturnLoginResponse() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        User user = User.builder()
                .id(1L)
                .refreshToken("valid-refresh-token")
                .isActive(true)
                .build();

        when(jwt.validateToken(request.getRefreshToken(), JWT.REFRESH_TOKEN_TYPE)).thenReturn(true);
        when(jwt.getUserIdFromToken(request.getRefreshToken())).thenReturn(1L);
        when(userRepository.findOne(any(Specification.class))).thenReturn(Optional.of(user));
        when(jwt.generateAccessToken(user.getId())).thenReturn("new-access-token");
        when(jwt.generateRefreshToken(user.getId())).thenReturn("new-refresh-token");
        when(userRepository.save(any(User.class))).thenReturn(user);

        LoginResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
    }

    @Test
    void refreshToken_WhenInvalidRefreshToken_ShouldThrowUnauthorizedException() {
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-refresh-token");

        when(jwt.validateToken(request.getRefreshToken(), JWT.REFRESH_TOKEN_TYPE)).thenThrow(new UnauthorizedException("invalid refresh token"));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authService.refreshToken(request)
        );

        assertEquals("invalid refresh token", exception.getMessage());
    }

    @Test
    void refreshToken_WhenUserNotFound_ShouldThrowUnauthorizedException() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");

        when(jwt.validateToken(request.getRefreshToken(), JWT.REFRESH_TOKEN_TYPE)).thenReturn(true);
        when(jwt.getUserIdFromToken(request.getRefreshToken())).thenReturn(1L);
        when(userRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authService.refreshToken(request)
        );

        assertEquals("invalid refresh token", exception.getMessage());
    }

    @Test
    void refreshToken_WhenUserIsInactive_ShouldThrowUnauthorizedException() {
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        User user = User.builder()
                .id(1L)
                .refreshToken("valid-refresh-token")
                .isActive(false)
                .build();

        when(jwt.validateToken(request.getRefreshToken(), JWT.REFRESH_TOKEN_TYPE)).thenReturn(true);
        when(jwt.getUserIdFromToken(request.getRefreshToken())).thenReturn(1L);
        when(userRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authService.refreshToken(request)
        );

        assertEquals("invalid refresh token", exception.getMessage());
    }
}