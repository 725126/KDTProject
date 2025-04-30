package org.zerock.b01.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.domain.user.User;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
    // 회원 ID(일련번호/외래키)로 회원 조회
    Optional<Partner> findByUser(User user);

    // 사업자등록번호 중복 확인
    boolean existsBypBusinessNo(String pBusinessNo);
}
