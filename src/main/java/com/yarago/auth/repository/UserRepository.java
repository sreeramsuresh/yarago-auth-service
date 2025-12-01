package com.yarago.auth.repository;

import com.yarago.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    /**
     * Find user by username or email for login
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    Optional<User> findByEmployeeId(String employeeId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByActiveTrue();

    List<User> findByAccountLockedTrue();

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = true")
    List<User> findActiveUsersByRole(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE u.branchId = :branchId AND u.active = true")
    List<User> findActiveUsersByBranch(@Param("branchId") Long branchId);

    @Query("SELECT u FROM User u WHERE u.department = :department AND u.active = true")
    List<User> findActiveUsersByDepartment(@Param("department") String department);

    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);
}
