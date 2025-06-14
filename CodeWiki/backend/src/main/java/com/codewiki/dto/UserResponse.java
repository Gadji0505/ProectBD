package com.codewiki.dto;

import com.codewiki.models.UserRole;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private int reputation;
    private String registrationDate;
}