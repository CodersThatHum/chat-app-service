package com.codersthathum.chat_app_service.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserParam {

    private Long id;

    private String name;

    private String email;

    private Boolean isActive;

}
