package org.zerock.b01.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.zerock.b01.security.CustomUserDetails;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String role = userDetails.getRole();

        if (role.equals("ADMIN") || role.equals("PRODUCTION") || role.equals("PURCHASING")) {
            response.sendRedirect("/internal/home"); // 내부 사용자 홈으로 리다이렉트
        } else if (role.equals("PARTNER")) {
            response.sendRedirect("/external/home"); // 외부 사용자 홈으로 리다이렉트
        } else {
            response.sendRedirect("/intro"); // 기본적으로 intro로 보내기
        }
    }
}
