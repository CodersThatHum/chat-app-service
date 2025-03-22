package com.codersthathum.chat_app_service.service.auth;

import com.codersthathum.chat_app_service.dto.auth.LoginRequest;
import com.codersthathum.chat_app_service.dto.auth.LoginResponse;
import com.codersthathum.chat_app_service.dto.auth.RegisterRequest;

public interface AuthService {

    void register(RegisterRequest param);

    LoginResponse login(LoginRequest param);

}
