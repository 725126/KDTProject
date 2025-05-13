package org.zerock.b01.domain.user;

import lombok.Getter;

@Getter
public enum UserRole {

    ADMIN("관리자", "bi-person-gear", "admin-profile"),
    PRODUCTION("생산부서", "bi-tools", "production-profile"),
    PURCHASING("구매부서", "bi-cart-check", "purchasing-profile"),
    PARTNER("협력업체", "bi-building", "partner-profile");

    private final String displayName;   // 한글 이름
    private final String iconClass;      // 부트스트랩 아이콘
    private final String profileClass;   // 추가 프로필 클래스

    UserRole(String displayName, String iconClass, String profileClass) {
        this.displayName = displayName;
        this.iconClass = iconClass;
        this.profileClass = profileClass;
    }

}
