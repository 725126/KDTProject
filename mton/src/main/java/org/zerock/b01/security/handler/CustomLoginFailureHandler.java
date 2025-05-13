package org.zerock.b01.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String referer = request.getHeader("Referer");
        String errorParam = "error"; // 기본 에러

        if (referer != null && referer.contains("/intro")) {
            // 인트로(로그인) 페이지에서 로그인 시
            response.sendRedirect("/intro?" + errorParam);
        } else {
            // 기본 로그인 페이지에서 로그인 시
            response.sendRedirect("/login?" + errorParam);
        }
    }
}
