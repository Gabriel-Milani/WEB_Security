package com.exemplo.secrest.dto;

import com.exemplo.secrest.enums.RoleName;

public record UserProfileDto(
        Long id,
        String email,
        String name,
        RoleName role
) {
}
