package org.zerock.b01.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.zerock.b01.dto.user.InternalUserCreateDTO;

@Controller
@Log4j2
@RequiredArgsConstructor
public class UserController {
    // 회원 관리 - 인트로(로그인)
    @GetMapping("/intro")
    public String introGet() {
        return "/page/user/intro";
    }

    // 회원 관리 - 로그인
    @GetMapping("/login")
    public String loginGet() {
        return "/page/user/login";
    }

    // 회원 관리 - 회원가입
    @GetMapping("/join")
    public String joinGet() {
        return "/page/user/join";
    }

    // 회원 관리 - 비밀번호 찾기
    @GetMapping("/find/pw")
    public String findPwGet() { return "/page/user/find/find-pw"; }

    // 회원 관리 - 비밀번호 찾기 > 비밀번호 재설정
    @GetMapping("/reset/pw")
    public String resetPwGet() {
        return "/page/user/find/reset-pw";
    }

    // 회원 관리 - 아이디 찾기
    @GetMapping("/find/id")
    public String findIdGet() {
        return "/page/user/find/find-id";
    }


    // 홈(내부 회원 전용)
    @GetMapping("/internal/home")
    public String internalHomeGet() {
        return "/page/user/internal-home";
    }

    // 홈(외부 회원 전용)
    @GetMapping("/external/home")
    public String externalHomeGet() {
        return "/page/user/external-home";
    }


    // 마이페이지 > 회원 탈퇴
    @GetMapping("/my/internal/account-delete")
    public String myPageAccountDeleteGet() {
        return "page/user/my/account-delete";
    }

    // 마이페이지 > 회원 정보 수정
    @GetMapping("/my/internal/account-edit")
    public String myPageAccountEditGet() {
        return "page/user/my/account-edit";
    }


    // 마이페이지 - 관리자 > 회원가입 승인
    @GetMapping("/my/admin/join-list")
    public String adminMyPageJoinListGet() {
        return "page/user/my/admin/join-list";
    }

    // 마이페이지 - 관리자 > 회원탈퇴 승인
    @GetMapping("/my/admin/delete-list")
    public String adminMyPageDeleteListGet() {
        return "page/user/my/admin/delete-list";
    }

    // 마이페이지 - 관리자 > 회원로그 기록
    @GetMapping("/my/admin/log-list")
    public String adminMyPageLogListGet() {
        return "page/user/my/admin/log-list";
    }

    // 마이페이지 - 관리자 > 회원 목록
    @GetMapping("/my/admin/user-list")
    public String adminMyPageUserListGet() {
        return "page/user/my/admin/user-list";
    }

    // 마이페이지 - 관리자 > 관리자 정보
    @GetMapping("/my/admin/edit")
    public String adminMyPageAdminEditGet() {
        return "page/user/my/admin/admin-edit";
    }
}
