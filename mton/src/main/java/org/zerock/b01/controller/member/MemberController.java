package org.zerock.b01.controller.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MemberController {

    // 회원 관리 - 인트로(로그인)
    @GetMapping("/intro")
    public String introPage() {
        return "/member/intro";
    }

    // 회원 관리 - 로그인
    @GetMapping("/login")
    public String loginPage() {
        return "/member/login";
    }

    // 회원 관리 - 회원가입
    @GetMapping("/join")
    public String joinPage() {
        return "/member/join";
    }

    // 메인
    @GetMapping("/main")
    public String test() {
        return "/member/main";
    }
}
