package org.zerock.b01.repository.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.user.User;
import org.zerock.b01.domain.user.UserRole;
import org.zerock.b01.domain.user.UserStatus;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "roleSet")
    @Query("select u from User u where u.userId = :userId")
    Optional<User> getWithRoles(String userId);

    // 이메일(uEmail)로 회원 존재 여부 확인
    boolean existsByuEmail(String uEmail);

    // 이메일(uEmail)로 회원 조회
    Optional<User> findByuEmail(String uEmail);

    Optional<User> findByResetToken(String token);

    @Query("SELECT u FROM User u WHERE u.uName = :uName AND u.uPhone = :uPhone AND u.userRole = :userRole")
    Optional<User> findUserByNamePhoneAndRole(@Param("uName") String uName,
                                              @Param("uPhone") String uPhone,
                                              @Param("userRole") UserRole userRole);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.uEmail = :uEmail AND u.uIsActive = :status")
    boolean existsByEmailAndStatus(@Param("uEmail") String uEmail,
                                   @Param("status") UserStatus status);

    // 회원가입 승인 대기 회원 조회
    List<User> findByuIsActive(UserStatus status);

    // 회원가입 승인 목록 페이징
    Page<User> findByuIsActive(UserStatus status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.uIsActive = :status AND u.userRole = :role")
    Page<User> findPendingUsersByRole(@Param("status") UserStatus status,
                                      @Param("role") UserRole role,
                                      Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.uIsActive = :status AND LOWER(u.uName) LIKE LOWER(CONCAT('%', :uName, '%'))")
    Page<User> searchByStatusAndName(@Param("status") UserStatus status,
                                     @Param("uName") String uName,
                                     Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.uIsActive = :status AND u.userRole = :role")
    Page<User> searchByStatusAndRole(@Param("status") UserStatus status,
                                     @Param("role") UserRole role,
                                     Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.uIsActive = :status AND u.userRole = :role AND LOWER(u.uName) LIKE LOWER(CONCAT('%', :uName, '%'))")
    Page<User> searchByStatusAndRoleAndName(@Param("status") UserStatus status,
                                            @Param("role") UserRole role,
                                            @Param("uName") String uName,
                                            Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR LOWER(u.uName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:role IS NULL OR u.userRole = :role)")
    Page<User> searchAllUsers(@Param("keyword") String keyword,
                              @Param("role") UserRole role,
                              Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (:status IS NULL OR u.uIsActive = :status) " +
            "AND (:role IS NULL OR u.userRole = :role) " +
            "AND (:keyword IS NULL OR LOWER(u.uName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<User> searchUsers(@Param("status") UserStatus status,
                           @Param("role") UserRole role,
                           @Param("keyword") String keyword,
                           Pageable pageable);
}
