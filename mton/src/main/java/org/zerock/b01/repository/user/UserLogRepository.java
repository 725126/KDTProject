package org.zerock.b01.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.user.UserLog;

public interface UserLogRepository extends JpaRepository<UserLog, Long> {
}
