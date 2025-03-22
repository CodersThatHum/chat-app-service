package com.codersthathum.chat_app_service.service.user;

import com.codersthathum.chat_app_service.dto.user.UserDTO;
import com.codersthathum.chat_app_service.entity.User;
import com.codersthathum.chat_app_service.exception.ResourceNotFoundException;
import com.codersthathum.chat_app_service.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserDTO getUserByID(Long id) {
        return this.userRepository.findById(id)
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

}
