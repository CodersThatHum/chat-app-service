package com.codersthathum.chat_app_service.controller;

import com.codersthathum.chat_app_service.dto.auth.LoginRequest;
import com.codersthathum.chat_app_service.dto.auth.LoginResponse;
import com.codersthathum.chat_app_service.dto.auth.RefreshTokenRequest;
import com.codersthathum.chat_app_service.dto.auth.RegisterRequest;
import com.codersthathum.chat_app_service.dto.http.HttpResponse;
import com.codersthathum.chat_app_service.dto.user.UserDTO;
import com.codersthathum.chat_app_service.service.auth.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/api/v1/auth/register")
    public ResponseEntity<HttpResponse<Void>> register(
            @RequestBody RegisterRequest param
    ) {
        this.authService.register(param);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        HttpResponse.success(
                                HttpStatus.CREATED,
                                "User registered successfully"
                        )
                );
    }

    @PostMapping(path = "/api/v1/auth/login")
    public ResponseEntity<HttpResponse<LoginResponse>> login(
            @RequestBody LoginRequest param
    ) {
        LoginResponse data = this.authService.login(param);
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        HttpResponse.success(
                                HttpStatus.OK,
                                "User logged in successfully",
                                data
                        )
                );
    }

    @GetMapping("/api/v1/auth/me")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<HttpResponse<UserDTO>> me(@AuthenticationPrincipal UserDTO userDTO) {
        return ResponseEntity.ok(
                HttpResponse.success(
                        HttpStatus.OK,
                        "User details retrieved successfully",
                        userDTO
                )
        );
    }

    @PostMapping("/api/v1/auth/refresh-token")
    public ResponseEntity<HttpResponse<LoginResponse>> refreshToken(
            @RequestBody RefreshTokenRequest refreshToken
    ) {
        LoginResponse data = this.authService.refreshToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        HttpResponse.success(
                                HttpStatus.OK,
                                "Refresh token generated successfully",
                                data
                        )
                );
    }
}
