package com.finance.dashboard.dto.request;

import com.finance.dashboard.enums.Role;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @Size(min=2,max = 100, message = "Name must be between 2 and 100")
    private String name;

    private Role role;




}
