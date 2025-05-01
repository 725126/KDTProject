package org.zerock.b01.controller.advice;

import lombok.RequiredArgsConstructor;
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
public class GlobalControllerAdvice {
    final PartnerRepository partnerRepository;

    // 권한
    @ModelAttribute("currentUserRole")
    public UserRole currentUserRole(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
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


}
