package org.zerock.b01.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.domain.user.User;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
    // 회원 ID(일련번호/외래키)로 회원 조회
    Optional<Partner> findByUser(User user);

    // 사업자등록번호 중복 확인
    boolean existsBypBusinessNo(String pBusinessNo);

    // 사업자등록번호로 회원 조회
    Optional<Partner> findBypBusinessNo(String pBusinessNo);

    // 협력업체 ID로 회사명 조회
    @Query("SELECT p.pCompany FROM Partner p WHERE p.partnerId = :id")
    Optional<String> findCompanyNameById(@Param("id") Long id);
}
