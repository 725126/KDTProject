package org.zerock.b01.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.user.User;
import org.zerock.b01.domain.user.UserStatus;
import org.zerock.b01.repository.user.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByuEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

            if (user.getUIsActive() == UserStatus.INACTIVE) {
                throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
            }

            return new CustomUserDetails(user);

        } catch (UsernameNotFoundException | BadCredentialsException e) {
            // 그대로 던짐
            throw e;
        } catch (Exception e) {
            // 기타 예외는 BadCredentials로 감싸서 던짐
            throw new BadCredentialsException("로그인 처리 중 오류가 발생했습니다.", e);
        }
    }
}
