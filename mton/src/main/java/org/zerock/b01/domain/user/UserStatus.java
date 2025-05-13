package org.zerock.b01.domain.user;

public enum UserStatus {
    PENDING,    // 가입 승인 대기 (기본값)
    ACTIVE,     // 승인 완료
    INACTIVE    // 탈퇴/비활성화 (로그인 차단)
}
