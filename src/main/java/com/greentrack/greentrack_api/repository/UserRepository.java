package com.greentrack.greentrack_api.repository;

import com.greentrack.greentrack_api.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Page<UserEntity> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username, 
            String email, 
            Pageable pageable
    );

    Page<UserEntity> findByUserStatus(String userStatus, Pageable pageable);
    
    @Query("SELECT u FROM UserEntity u WHERE " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :term, '%'))) " +
           "AND u.userStatus = 'ACTIVE'")
    Page<UserEntity> searchUsers(@Param("term") String term, Pageable pageable);
}
