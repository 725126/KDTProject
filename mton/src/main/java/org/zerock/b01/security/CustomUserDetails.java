package org.zerock.b01.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.zerock.b01.domain.user.User;
import org.zerock.b01.domain.user.UserRole;
import org.zerock.b01.domain.user.UserStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L; // ✅ 명시적으로 버전 고정

    private User user;

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (true = 만료되지 않음)
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 여부 (true = 잠기지 않음)
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부 (true = 만료되지 않음)
    }

    @Override
    public boolean isEnabled() {
        return user.getUIsActive() == UserStatus.ACTIVE;
        // uIsActive 상태가 ACTIVE일 때만 로그인 가능
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "ROLE_" + user.getUserRole().name();
                // ROLE_ 접두어 붙여줘야 Security 규칙에 맞음
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {

        return user.getUPassword(); // 비밀번호 필드명
    }

    @Override
    public String getUsername() {

        return user.getUEmail(); // 로그인 아이디 (uEmail)
    }

    // 회원 정보
    public User getUser() {
        return user;
    }

    // 회원 이름
    public String getUName() {

        return user.getUName();
    }

    // 회원 종류 (String)
    public String getRole() {

        return user.getUserRole().name();
    }

    // 회원 종류
    public UserRole getUserRole() {

        return user.getUserRole();
    }
}
