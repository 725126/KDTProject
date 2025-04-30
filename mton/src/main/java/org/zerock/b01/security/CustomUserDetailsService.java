package org.zerock.b01.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.user.User;
import org.zerock.b01.repository.user.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByuEmail(username);

        return userOptional.map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

    }
}
