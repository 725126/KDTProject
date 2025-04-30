package org.zerock.b01.repository.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.user.User;

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

    @Query("SELECT u FROM User u WHERE u.uName = :uName AND u.uPhone = :uPhone")
    Optional<User> findByNameAndPhone(@Param("uName") String uName,
                                      @Param("uPhone") String uPhone);


}
