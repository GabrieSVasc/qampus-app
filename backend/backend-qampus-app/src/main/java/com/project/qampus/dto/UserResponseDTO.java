package com.project.qampus.dto;

import com.project.qampus.model.User;

public record UserResponseDTO(String id, String name, String email) {
    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail());
    }
}