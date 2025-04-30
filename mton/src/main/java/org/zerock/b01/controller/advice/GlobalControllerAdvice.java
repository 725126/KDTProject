package org.zerock.b01.controller.advice;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.zerock.b01.domain.user.UserRole;
import org.zerock.b01.security.CustomUserDetails;


@ControllerAdvice
public class GlobalControllerAdvice {
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

}
