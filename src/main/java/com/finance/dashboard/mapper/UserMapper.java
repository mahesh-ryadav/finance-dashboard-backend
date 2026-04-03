package com.finance.dashboard.mapper;

import com.finance.dashboard.dto.request.CreateUserRequest;
import com.finance.dashboard.dto.request.RegisterRequest;
import com.finance.dashboard.dto.response.PagedResponse;
import com.finance.dashboard.dto.response.UserResponse;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.Role;
import com.finance.dashboard.enums.UserStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

//    Mapper method to map User entity to user response dto
    public static UserResponse toResponse(User user){
        if(user==null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

//    method for mapping RegisterRequest dto to User entity mapping
    public static User toEntity(RegisterRequest request){
        if (request==null) return null;

        return User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase().trim())
                .password(request.getPassword())// it is encoded in service layer
                .role(request.getRole()!=null?request.getRole(): Role.VIEWER)
                .status(UserStatus.ACTIVE)
                .isDeleted(false)
                .build();
    }


//    Mapper method to create user by admin
    public static User toEntity(CreateUserRequest request){
        if (request==null) return null;

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword()) // encode in service layer
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .isDeleted(false)
                .build();
    }


    // Mapper method to get paged response
    public static PagedResponse<UserResponse> toPagedResponse(Page<User> page){
        List<UserResponse>  content = page.getContent()
                .stream()
                .map(UserMapper::toResponse)
                .toList();


        return PagedResponse.<UserResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }





}
