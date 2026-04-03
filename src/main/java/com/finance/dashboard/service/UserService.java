package com.finance.dashboard.service;

import com.finance.dashboard.dto.request.CreateUserRequest;
import com.finance.dashboard.dto.request.UpdateUserRequest;
import com.finance.dashboard.dto.response.PagedResponse;
import com.finance.dashboard.dto.response.UserResponse;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.UserStatus;
import com.finance.dashboard.exception.DuplicateResourceException;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.mapper.UserMapper;
import com.finance.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    // Get all users with pagination
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAllActiveUsers(pageable);
        return UserMapper.toPagedResponse(users);
    }

    // Get user by ID
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUserById(id);
        return UserMapper.toResponse(user);
    }

    // Create user (ADMIN)
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {

        // Check duplicate email
        if (userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = UserMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }

    //  Update user
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserById(id);

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        User updatedUser = userRepository.save(user);
        return UserMapper.toResponse(updatedUser);
    }

    // Toggle user status ACTIVE / INACTIVE
    @Transactional
    public UserResponse toggleUserStatus(Long id) {
        User user = findUserById(id);

        if (user.getStatus() == UserStatus.ACTIVE) {
            user.setStatus(UserStatus.INACTIVE);
        } else {
            user.setStatus(UserStatus.ACTIVE);
        }

        User updatedUser = userRepository.save(user);
        return UserMapper.toResponse(updatedUser);
    }

    // Soft delete user
    @Transactional
    public void deleteUser(Long id) {
        User user = findUserById(id);
        user.setIsDeleted(true);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    // Private helper
    private User findUserById(Long id) {
        return userRepository.findById(id)
                .filter(u -> !u.getIsDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}