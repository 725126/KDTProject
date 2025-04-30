package org.zerock.b01.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.user.UserCreateDTO;
import org.zerock.b01.dto.user.UserResponseDTO;
import org.zerock.b01.security.CustomUserDetails;
import org.zerock.b01.service.user.UserService;

import java.util.Map;
import java.util.Optional;

@Controller
@Log4j2
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 마이페이지 링크
    // 마이페이지 작업

    // 회원 관리 - 인트로(로그인)
    @GetMapping("/intro")
    public String introGet() {
        return "/page/user/intro";
    }

    // [내부직원] 로그인 시 홈 화면
    @GetMapping("/internal/home")
    public String homePageInternal(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        log.info("[내부직원] 로그인: {}", customUserDetails);

        // *** 중요 ***
        // 로그인을 하지 않은 상태에서 @AuthenticationPrincipal을 주입받으면 null이 들어올 수 있음.
        if (customUserDetails == null) {
            // 로그인 안 되어 있으면 시작 페이지로
            return "redirect:/intro";
        }

        return "page/user/internal-home"; // home.html 열기
    }

    // [협력업체] 회원 로그인 시 홈 화면
    @GetMapping("/external/home")
    public String homePageExternal(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        log.info("[협력업체] 회원 로그인: {}", customUserDetails);

        // *** 중요 ***
        // 로그인을 하지 않은 상태에서 @AuthenticationPrincipal을 주입받으면 null이 들어올 수 있음.
        if (customUserDetails == null) {
            // 로그인 안 되어 있으면 시작 페이지로
            return "redirect:/intro";
        }

        return "page/user/external-home"; // home.html 열기
    }

    // 이메일(회원 아이디) 중복 확인
    @GetMapping("/check/email")
    @ResponseBody
    public boolean checkEmail(@RequestParam String email) {
        return !userService.isExistedUEmail(email);
        // true 반환 → 사용 가능
        // false 반환 → 이미 존재 (사용 불가)
    }

    // 사업자등록번호 중복 확인
    @GetMapping("/check/business-no")
    @ResponseBody
    public ResponseEntity<Boolean> checkBusinessNo(@RequestParam String businessNo) {
        log.info("[협력업체] 회원가입 - 사업자등록번호 중복확인: {}", businessNo);

        boolean isAvailable = userService.isBusinessNoAvailable(businessNo);
        return ResponseEntity.ok(isAvailable); // true면 사용 가능, false면 이미 존재
    }

    // 회원 관리 - 로그인
    @GetMapping("/login")
    public String loginGet() {
        return "page/user/login";
    }

    // 회원 관리 - 회원가입
    @GetMapping("/join")
    public String joinGet() {
        return "page/user/join";
    }

    // 회원 관리 - 내부직원 회원가입 (GET)
    @GetMapping("/join/inner")
    public String joinInnerGet(Model model) {
        model.addAttribute("userCreateDTO", new UserCreateDTO());
        return "page/user/inner-join";
    }

    // 회원 관리 - 내부직원 회원가입 (POST)
    @PostMapping("/join/inner")
    public String joinInnerProc(@Valid UserCreateDTO request, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.getAllErrors());
            model.addAttribute("errors", bindingResult.getFieldErrors());

            return "page/user/inner-join"; // 가입 페이지로 다시 리턴
        }

        log.info(request.toString());

        if (userService.createUser(request)) {
            log.info("[내부직원] 회원가입 완료");
        } else {
            log.info("[내부직원] 회원가입 에러");
        }

        return "redirect:/login?alert=joinSuccess";
    }

    // 회원 관리 - 협력업체 회원가입 (GET)
    @GetMapping("/join/partner")
    public String joinPartnerGet(Model model) {
        model.addAttribute("userCreateDTO", new UserCreateDTO());
        return "page/user/partner-join";
    }

    // 회원 관리 - 협력업체 회원가입 (POST)
    @PostMapping("/join/partner")
    public String joinPartnerProc(@Valid UserCreateDTO request, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.getAllErrors());
            model.addAttribute("errors", bindingResult.getFieldErrors());

            return "page/user/partner-join"; // 가입 페이지로 다시 리턴
        }

        log.info(request.toString());

        if (userService.createUser(request)) {
            log.info("[협력업체] 회원가입 완료");
        } else {
            log.info("[협력업체] 회원가입 에러");
        }

        return "redirect:/login?alert=joinSuccess";
    }

    // 회원 관리 - 비밀번호 찾기
    @GetMapping("/find/pw")
    public String findPwGet() { return "page/user/find/find-pw"; }

    // 회원 관리 - 비밀번호 찾기 > 비밀번호 재설정
    @GetMapping("/reset/pw")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        log.info("[reset/pw] 진입. token={}", token);

        boolean validToken = userService.checkResetTokenValid(token);

        if (!validToken) {
            log.info("[비밀번호 재설정] 잘못된 접근: {}", token);
            return "redirect:/intro?error=invalidToken"; // 잘못된 접근 시 intro로 보내기
        }

        model.addAttribute("token", token);
        return "page/user/find/reset-pw"; // 정상 토큰이면 폼 열어줌
    }

    // 회원관리 - 비밀번호 찾기 > 비밀번호 재설정 (POST)
    @PostMapping("/reset/pw")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword) {

        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/reset/pw?token=" + token + "&error=passwordMismatch";
        }

        userService.resetPassword(token, newPassword);
        return "redirect:/login?alert=resetSuccess"; // 변경 완료 후 로그인 페이지로 이동
    }

    // 회원관리 - 비밀번호 찾기 > 비밀번호 재설정 (GET)
    @PostMapping("/find/pw")
    @ResponseBody
    public Map<String, Boolean> sendResetPasswordLink(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        userService.sendResetPasswordEmail(email);
        return Map.of("success", true);
    }

    // 회원 관리 - 아이디 찾기
    @GetMapping("/find/id")
    public String findIdGet() {
        return "page/user/find/find-id";
    }

    @PostMapping("/find/id")
    @ResponseBody
    public Map<String, Object> findUserId(@RequestBody Map<String, String> request) {
        String uName = request.get("uName");
        String uPhone = request.get("uPhone");

        log.info(uName + " " + uPhone);

        Optional<String> foundEmail = userService.findEmailByNameAndPhone(uName, uPhone);

        return foundEmail.<Map<String, Object>>map(s -> Map.of("foundEmail", s)).orElseGet(() -> Map.of("notFound", true));
    }

    // 마이페이지 > 회원 탈퇴
    @GetMapping("/internal/my/account-delete")
    public String myPageAccountDeleteGetIn() {
        return "page/user/my/account-delete";
    }

    // 마이페이지 > 내 정보
    @GetMapping("/internal/my/account-edit")
    public String myPageAccountEditGetIn() {
        return "page/user/my/account-edit";
    }

    // 마이페이지 > 회원 탈퇴
    @GetMapping("/external/my/account-delete")
    public String myPageAccountDeleteGetEx() {
        return "page/user/my/account-delete";
    }

    // 마이페이지 > 내 정보
    @GetMapping("/external/my/account-edit")
    public String myPageAccountEditGetEx() {
        return "page/user/my/account-edit";
    }


    // 마이페이지 - 관리자 > 회원가입 승인
    @GetMapping("/admin/my/join-list")
    public String adminMyPageJoinListGet() {
        return "page/user/my/admin/join-list";
    }

    // 마이페이지 - 관리자 > 회원탈퇴 승인
    @GetMapping("/admin/my/delete-list")
    public String adminMyPageDeleteListGet() {
        return "page/user/my/admin/delete-list";
    }

    // 마이페이지 - 관리자 > 회원로그 기록
    @GetMapping("/admin/my/log-list")
    public String adminMyPageLogListGet() {
        return "page/user/my/admin/log-list";
    }

    // 마이페이지 - 관리자 > 회원 목록
    @GetMapping("/admin/my/user-list")
    public String adminMyPageUserListGet() {
        return "page/user/my/admin/user-list";
    }

    // 마이페이지 - 관리자 > 관리자 정보
    @GetMapping("/admin/my/edit")
    public String adminMyPageAdminEditGet() {
        return "page/user/my/admin/admin-edit";
    }
}
