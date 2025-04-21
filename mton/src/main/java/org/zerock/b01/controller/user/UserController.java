package org.zerock.b01.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2
@RequiredArgsConstructor
public class UserController {

    // 회원 관리 - 인트로(로그인)
    @GetMapping("/intro")
    public String introPage() {
        return "/user/intro";
    }

    // 회원 관리 - 로그인
    @GetMapping("/login")
    public String loginPage() {
        return "/user/login";
    }

    // 회원 관리 - 회원가입
    @GetMapping("/join")
    public String joinPage() {
        return "/user/join";
    }


    // 회원 관리 - 비밀번호 찾기
    @GetMapping("/find/pw")
    public String findPwPage() {
        return "/user/find-pw";
    }

    // 회원 관리 - 비밀번호 찾기 > 비밀번호 재설정
    @GetMapping("/reset/pw")
    public String resetPwPage() {
        return "/user/reset-pw";
    }
    // 회원 관리 - 아이디 찾기
    @GetMapping("/find/id")
    public String findIdPage() {
        return "/user/find-id";
    }


    // 홈(내부 회원 전용)
    @GetMapping("/internal/home")
    public String internalHomePage() {
        return "/user/internal-home";
    }

    // 홈(외부 회원 전용)
    @GetMapping("/external/home")
    public String externalHomePage() {
        return "/user/external-home";
    }

    // 마이페이지 - 관리자
    @GetMapping("/my/admin")
    public String adminMyPage() {
        return "/user/join-list";
    }
}
