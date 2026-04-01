package com.finance.dashboard.dto.response;

import com.finance.dashboard.enums.Role;
import com.finance.dashboard.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;

    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
