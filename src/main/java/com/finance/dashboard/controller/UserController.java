package com.finance.dashboard.controller;

import com.finance.dashboard.dto.request.CreateUserRequest;
import com.finance.dashboard.dto.request.UpdateUserRequest;
import com.finance.dashboard.dto.response.ApiResponse;
import com.finance.dashboard.dto.response.PagedResponse;
import com.finance.dashboard.dto.response.UserResponse;
import com.finance.dashboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "ADMIN only — manage users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    // GET /api/v1/users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users (paginated)")
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<UserResponse> response = userService.getAllUsers(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Users fetched successfully", response));
    }

    // GET /api/v1/users/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id) {

        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(
                ApiResponse.success("User fetched successfully", response));
    }

    // POST /api/v1/users
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }

    // PUT /api/v1/users/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user name or role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("User updated successfully", response));
    }

    // PATCH /api/v1/users/{id}/status
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle user status ACTIVE / INACTIVE")
    public ResponseEntity<ApiResponse<UserResponse>> toggleStatus(
            @PathVariable Long id) {

        UserResponse response = userService.toggleUserStatus(id);
        return ResponseEntity.ok(
                ApiResponse.success("User status updated successfully", response));
    }

    // DELETE /api/v1/users/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete a user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);
        return ResponseEntity.ok(
                ApiResponse.success("User deleted successfully"));
    }
}