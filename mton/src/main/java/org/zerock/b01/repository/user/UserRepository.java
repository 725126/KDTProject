package org.zerock.b01.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByuEmail(String uEmail);
}
