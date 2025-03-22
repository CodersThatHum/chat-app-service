package com.codersthathum.chat_app_service.service.user;

import com.codersthathum.chat_app_service.dto.user.UserDTO;

public interface UserService {
    UserDTO getUserByID(Long id);
}
