package org.zerock.b01.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.domain.user.User;
import org.zerock.b01.domain.user.UserLog;
import org.zerock.b01.domain.user.UserRole;
import org.zerock.b01.dto.user.FindIdDTO;
import org.zerock.b01.dto.user.UserCreateDTO;
import org.zerock.b01.dto.user.UserResponseDTO;
import org.zerock.b01.dto.user.UserUpdateDTO;
import org.zerock.b01.security.CustomUserDetails;
import org.zerock.b01.service.user.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@Log4j2
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원 관리 - 인트로(로그인)
    @GetMapping("/intro")
    public String introGet(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String role = userDetails.getUserRole().name();
            switch (role) {
                case "ADMIN":
                    return "redirect:/internal/home";  // 관리자 페이지
                case "PRODUCTION":
                    return "redirect:/internal/home";      // 생산부서 홈
                case "PURCHASING":
                    return "redirect:/internal/home";      // 구매부서 홈
                case "PARTNER":
                    return "redirect:/external/home";      // 협력업체 홈
            }
        }

        return "/page/user/intro";
    }

    // [내부직원] 로그인 시 홈 화면
    @GetMapping("/internal/home")
    public String homePageInternal(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
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
    public String homePageExternal(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
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
    public String loginGet(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String role = userDetails.getUserRole().name();
            switch (role) {
                case "ADMIN":
                    return "redirect:/internal/home";  // 관리자 페이지
                case "PRODUCTION":
                    return "redirect:/internal/home";      // 생산부서 홈
                case "PURCHASING":
                    return "redirect:/internal/home";      // 구매부서 홈
                case "PARTNER":
                    return "redirect:/external/home";      // 협력업체 홈
            }
        }

        return "page/user/login";
    }

    // 회원 관리 - 회원가입
    @GetMapping("/join")
    public String joinGet(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String role = userDetails.getUserRole().name();
            switch (role) {
                case "ADMIN":
                    return "redirect:/internal/home";  // 관리자 페이지
                case "PRODUCTION":
                    return "redirect:/internal/home";      // 생산부서 홈
                case "PURCHASING":
                    return "redirect:/internal/home";      // 구매부서 홈
                case "PARTNER":
                    return "redirect:/external/home";      // 협력업체 홈
            }
        }

        return "page/user/join";
    }

    // 회원 관리 - 내부직원 회원가입 (GET)
    @GetMapping("/join/inner")
    public String joinInnerGet(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String role = userDetails.getUserRole().name();
            switch (role) {
                case "ADMIN":
                    return "redirect:/internal/home";  // 관리자 페이지
                case "PRODUCTION":
                    return "redirect:/internal/home";      // 생산부서 홈
                case "PURCHASING":
                    return "redirect:/internal/home";      // 구매부서 홈
                case "PARTNER":
                    return "redirect:/external/home";      // 협력업체 홈
            }
        }

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
    public String joinPartnerGet(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails != null) {
            String role = userDetails.getUserRole().name();
            switch (role) {
                case "ADMIN":
                    return "redirect:/internal/home";  // 관리자 페이지
                case "PRODUCTION":
                    return "redirect:/internal/home";      // 생산부서 홈
                case "PURCHASING":
                    return "redirect:/internal/home";      // 구매부서 홈
                case "PARTNER":
                    return "redirect:/external/home";      // 협력업체 홈
            }
        }

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
    public String findPwGet(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String role = userDetails.getUserRole().name();
            switch (role) {
                case "ADMIN":
                    return "redirect:/internal/home";  // 관리자 페이지
                case "PRODUCTION":
                    return "redirect:/internal/home";      // 생산부서 홈
                case "PURCHASING":
                    return "redirect:/internal/home";      // 구매부서 홈
                case "PARTNER":
                    return "redirect:/external/home";      // 협력업체 홈
            }
        }

        return "page/user/find/find-pw";
    }

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
        if(userService.isExistedUEmail(email)) {
            userService.sendResetPasswordEmail(email);
            return Map.of("success", true);
        } else {
            return Map.of("success", false);
        }
    }

    // 회원 관리 - 아이디 찾기 Get
    @GetMapping("/find/id")
    public String findIdGet(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails != null) {
            String role = userDetails.getUserRole().name();
            switch (role) {
                case "ADMIN":
                    return "redirect:/internal/home";  // 관리자 페이지
                case "PRODUCTION":
                    return "redirect:/internal/home";      // 생산부서 홈
                case "PURCHASING":
                    return "redirect:/internal/home";      // 구매부서 홈
                case "PARTNER":
                    return "redirect:/external/home";      // 협력업체 홈
            }
        }

        return "page/user/find/find-id";
    }

    // 회원 관리 - 아이디 찾기 Post
    @PostMapping("/find/id")
    @ResponseBody
    public Map<String, Object> findUserId(@RequestBody FindIdDTO dto) {

        log.info("[아이디 찾기]: {}", dto);

        if ("PARTNER".equals(dto.getUserRole())) {
            String businessNo = Optional.ofNullable(dto.getPBusinessNo()).orElse("").trim();
            if (businessNo.isEmpty()) {
                return Map.of("notFound", true, "message", "사업자등록번호 누락");
            }

            log.info("[아이디 찾기] 협력업체 > 사업자등록번호: {}", businessNo);
        }

        return userService.findEmailByFindIdDTO(dto)
                .<Map<String, Object>>map(email -> Map.of("foundEmail", email))
                .orElseGet(() -> Map.of("notFound", true));
    }

    // [내부직원] 마이페이지 > 회원 탈퇴
    @GetMapping("/internal/my/account-delete")
    public String myPageAccountDeleteGetIn(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {

        UserResponseDTO userResponseDTO = userService.getUserInfoById(customUserDetails.getUserId());
        model.addAttribute("userResponseDTO", userResponseDTO);

        return "page/user/my/account-delete";
    }

    // [내부직원] 마이페이지 > 회원정보
    @GetMapping("/internal/my/account-edit")
    public String myPageAccountEditGetIn(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {

        UserResponseDTO userResponseDTO = userService.getUserInfoById(customUserDetails.getUserId());
        model.addAttribute("userResponseDTO", userResponseDTO);

        return "page/user/my/account-edit";
    }

    // [협력업체] 마이페이지 > 회원 탈퇴
    @GetMapping("/external/my/account-delete")
    public String myPageAccountDeleteGetEx(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {

        UserResponseDTO userResponseDTO = userService.getUserInfoById(customUserDetails.getUserId());
        model.addAttribute("userResponseDTO", userResponseDTO);

        return "page/user/my/account-delete";
    }

    // [협력업체] 마이페이지 > 회원정보
    @GetMapping("/external/my/account-edit")
    public String myPageAccountEditGetEx(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {

        UserResponseDTO userResponseDTO = userService.getUserInfoById(customUserDetails.getUserId());
        model.addAttribute("userResponseDTO", userResponseDTO);

        return "page/user/my/account-edit";
    }

    // [공통] 마이페이지 > 회원탈퇴
    @PostMapping("/withdraw")
    public String withdraw(@RequestParam Long userId,
                           @RequestParam String reason,
                           @RequestParam(required = false) String customReason,
                           @AuthenticationPrincipal CustomUserDetails currentUser,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {

        String finalReason = reason.equals("other") ? customReason : reason;

        userService.deactivateUser(userId, finalReason);

        // 탈퇴 성공 flag 추가
        redirectAttributes.addFlashAttribute("withdrawSuccess", true);

        // 현재 로그인한 사용자가 스스로 탈퇴한 경우 → 로그아웃
        if (currentUser.getUserId().equals(userId)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            SecurityContextHolder.clearContext();
            return "redirect:/login?logout";
        }

        // 관리자가 타인 계정 탈퇴시 → 로그아웃 없이 관리자 마이페이지로 이동
        return "redirect:/admin/my/user/" + userId;
    }

    // [공통] 마이페이지 > 회원정보 > 비밀번호 변경
    @PostMapping("/my/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam Long userId,
                                 RedirectAttributes redirectAttributes) {

        // 1. 사용자 정보 조회
        User user = userService.getUserById(userId);

        // 2. 현재 비밀번호 검증
        if (!userService.checkPasswordMatch(currentPassword, user.getUPassword())) {
            redirectAttributes.addFlashAttribute("errorPw", "현재 비밀번호가 일치하지 않습니다.");
            redirectAttributes.addFlashAttribute("activeTab", "passwordTab");

            // [협력업체] 회원인 경우
            if (user.getUserRole().equals(UserRole.PARTNER)) {
                return "redirect:/external/my/account-edit";
            }

            return "redirect:/internal/my/account-edit";
        }

        // 3. 새 비밀번호 인코딩 및 저장
        userService.changePassword(user, newPassword);

        redirectAttributes.addFlashAttribute("successPw", "비밀번호가 변경되었습니다.");
        redirectAttributes.addFlashAttribute("activeTab", "passwordTab");

        // [협력업체] 회원인 경우
        if (user.getUserRole().equals(UserRole.PARTNER)) {
            return "redirect:/external/my/account-edit";
        }

        return "redirect:/internal/my/account-edit";
    }

    // [공통] 마이페이지 > 회원정보 수정
    @PostMapping("/my/account-edit")
    public String updateAccount(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @ModelAttribute UserUpdateDTO dto,
                                RedirectAttributes redirectAttributes) {
        log.info("[공통] 회원정보 수정: {}", dto);

        try {
            userService.updateUserInfo(dto.getUserId(), dto);
            redirectAttributes.addFlashAttribute("successEdit", "회원정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorEdit", "회원정보 수정 중 오류가 발생했습니다.");
        }

        if (userDetails.getUserRole().equals(UserRole.PARTNER)) {
            // [협력업체] 회원인 경우
            return "redirect:/external/my/account-edit";
        } else if (userDetails.getUserRole().equals(UserRole.ADMIN)) {
            // [관리자]인 경우
            return "redirect:/admin/my/user/" + dto.getUserId();
        }

        return "redirect:/internal/my/account-edit";
    }

    // [관리자] 관리페이지 > 회원가입 승인
    @GetMapping("/admin/my/join-list")
    public String showPendingUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "newest") String sort,
            Model model
    ) {
        Pageable pageable;

        // 정렬 조건 분기
        switch (sort) {
            case "nameAsc" -> pageable = PageRequest.of(page, size, Sort.by("uName").ascending());
            case "newest" -> pageable = PageRequest.of(page, size, Sort.by("creDate").descending());
            case "oldest" -> pageable = PageRequest.of(page, size, Sort.by("creDate").ascending());
            default -> pageable = PageRequest.of(page, size);
        }

        Page<User> pendingUsers = userService.getPendingUsersWithFilter(keyword, role, pageable);

        model.addAttribute("pendingUsers", pendingUsers);
        model.addAttribute("currentPage", pendingUsers.getNumber() + 1);
        model.addAttribute("totalPages", pendingUsers.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("selectedSize", size);

        return "page/user/my/admin/join-list";
    }

    // [관리자] 관리페이지 > 회원가입 승인
    @PutMapping("/admin/my/approve")
    @ResponseBody
    public ResponseEntity<?> approveUser(@RequestBody Map<String, String> payload) {
        String uEmail = payload.get("uEmail");
        userService.activateUser(uEmail);
        return ResponseEntity.ok().build();
    }

    // 마이페이지 - 관리자 > 회원탈퇴 승인
    @GetMapping("/admin/my/delete-list")
    public String adminMyPageDeleteListGet() {
        return "page/user/my/admin/delete-list";
    }

    // 마이페이지 - 관리자 > 회원로그 기록
    @GetMapping("/admin/my/log-list")
    public String adminMyPageLogListGet(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "6") int size,
                                        Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserLog> userLogs = userService.getPagedLogsSortedByDateDesc(pageable);

//        List<UserLog> userLogs = userService.getAllLogsSortedByDateDesc();
        model.addAttribute("userLogs", userLogs);
        model.addAttribute("currentPage", userLogs.getNumber() + 1);
        model.addAttribute("totalPages", userLogs.getTotalPages());

        return "page/user/my/admin/log-list";
    }

    // 마이페이지 - 관리자 > 회원 목록
    @GetMapping("/admin/my/user-list")
    public String showUserList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "newest") String sort,
            Model model
    ) {
        Pageable pageable;

        // 정렬 조건 분기
        switch (sort) {
            case "nameAsc" -> pageable = PageRequest.of(page, size, Sort.by("uName").ascending());
            case "newest" -> pageable = PageRequest.of(page, size, Sort.by("creDate").descending());
            case "oldest" -> pageable = PageRequest.of(page, size, Sort.by("creDate").ascending());
            default -> pageable = PageRequest.of(page, size);
        }


        log.info("회원역할: " + role);

        Page<User> userPage = userService.findUsersByFilters(keyword, role, status, pageable);

        model.addAttribute("userList", userPage.getContent());
        model.addAttribute("currentPage", userPage.getNumber() + 1);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRole", role);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("selectedSize", size);

        return "page/user/my/admin/user-list";
    }

    // 관리자가 회원의 회원정보 수정
    @GetMapping("/admin/my/user/{id}")
    public String viewUser(@PathVariable("id") Long userId, Model model) {
        UserResponseDTO userDto = userService.getUserInfoById(userId);
        model.addAttribute("userResponseDTO", userDto);

        return "page/user/my/account-edit";
    }

    @GetMapping("/admin/my/user-deleted/{id}")
    public String deleteUser(@PathVariable("id") Long userId, Model model) {
        UserResponseDTO userDto = userService.getUserInfoById(userId);
        model.addAttribute("userResponseDTO", userDto);

        return "page/user/my/account-delete";
    }

    // 마이페이지 - 관리자 > 관리자 정보
    @GetMapping("/admin/my/edit")
    public String adminMyPageAdminEditGet() {
        return "page/user/my/admin/admin-edit";
    }
}
