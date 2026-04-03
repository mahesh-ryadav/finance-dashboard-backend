package com.finance.dashboard.service;

import com.finance.dashboard.dto.request.LoginRequest;
import com.finance.dashboard.dto.request.RegisterRequest;
import com.finance.dashboard.dto.response.AuthResponse;
import com.finance.dashboard.entity.User;
import com.finance.dashboard.exception.DuplicateResourceException;
import com.finance.dashboard.exception.ResourceNotFoundException;
import com.finance.dashboard.exception.UserInactiveException;
import com.finance.dashboard.mapper.UserMapper;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.security.CustomUserDetailsService;
import com.finance.dashboard.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository       userRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtUtil              jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    // Register
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // Step 1: Check duplicate email
        if (userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Step 2: Map to entity
        User user = UserMapper.toEntity(request);

        // Step 3: Encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Step 4: Save user
        User savedUser = userRepository.save(user);

        // Step 5: Generate token
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtUtil.generateToken(
                userDetails,
                savedUser.getId(),
                savedUser.getRole().name()
        );

        // Step 6: Return response
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    // Login
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        // Step 1: Find user
        User user = userRepository
                .findByEmailAndIsDeletedFalse(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        // Step 2: Check user is active
        if (user.getStatus().name().equals("INACTIVE")) {
            throw new UserInactiveException(request.getEmail());
        }

        // Step 3: Authenticate
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Step 4: Generate token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(
                userDetails,
                user.getId(),
                user.getRole().name()
        );

        // Step 5: Return response
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}