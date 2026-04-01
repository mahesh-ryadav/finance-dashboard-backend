package com.finance.dashboard.repository;

import com.finance.dashboard.entity.User;
import com.finance.dashboard.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // It will be used in login & Spring Security
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    //It will check duplicate email before registration
    boolean existsByEmailAndIsDeletedFalse(String email);

    //It will give all non-deleted users with pagination
    @Query("SELECT u FROM User u WHERE u.isDeleted = false")
    Page<User> findAllActiveUsers(Pageable pageable);

    //Get users by status with pagination
    @Query("SELECT u FROM User u WHERE u.status = :status AND u.isDeleted = false")
    Page<User> findByStatus(UserStatus status, Pageable pageable);
}