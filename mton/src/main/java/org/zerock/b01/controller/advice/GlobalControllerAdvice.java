package org.zerock.b01.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.domain.user.UserRole;
import org.zerock.b01.repository.user.PartnerRepository;
import org.zerock.b01.security.CustomUserDetails;
import org.zerock.b01.service.user.UserService;

import java.util.Optional;


@ControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class GlobalControllerAdvice {
    final PartnerRepository partnerRepository;

    // 권한
    @ModelAttribute("currentUserRole")
    public UserRole currentUserRole(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Current user role: {}", userDetails);

        if (userDetails != null) {
            log.info("Current user role: {}", userDetails.getUserRole());
            return userDetails.getUserRole(); // "ADMIN", "PARTNER" 등
        }
        return null;
    }

    // 이름
    @ModelAttribute("currentUserName")
    public String currentUserName(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            return userDetails.getUName(); // 이름 필드
        }
        return null;
    }


    // 회사명
    @ModelAttribute("currentPartnerCompany")
    public String currentPartnerCompany(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            if (userDetails.getUserRole().equals(UserRole.PARTNER)) {
                Optional<Partner> partnerOptional = partnerRepository.findByUser(userDetails.getUser());
                if (partnerOptional.isPresent()) {
                    return partnerOptional.get().getPCompany();
                }
            }
        }
        return null;
    }

    // 현재 페이지
    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        return request.getRequestURI(); // e.g. "/admin/my/user-list"
    }


}
