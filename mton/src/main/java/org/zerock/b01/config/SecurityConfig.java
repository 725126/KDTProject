package org.zerock.b01.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.zerock.b01.security.CustomUserDetailsService;
import org.zerock.b01.security.handler.CustomLoginFailureHandler;
import org.zerock.b01.security.handler.CustomLoginSuccessHandler;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final DataSource dataSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, CustomLoginSuccessHandler customLoginSuccessHandler, CustomLoginFailureHandler customLoginFailureHandler) throws Exception {
        httpSecurity
                // 인증/인가 규칙을 설정하는 부분 시작
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/assets").permitAll()
                        // '/intro', '/login', '/join' 경로는 로그인 없이 누구나 접근 가능하게 허용
                        .requestMatchers("/intro", "/login", "/join/**", "/find/**").permitAll()
                        // 비밀번호 재설정 페이지 누구나 접근 가능 (컨트롤러에 토큰 검증로직으로 접근 제한)
                        .requestMatchers("/reset/pw", "/reset/pw*").permitAll()
                        // 이메일(회원 아이디) 중복확인 링크 누구나 접근 가능
                        .requestMatchers("/check/email").permitAll()
                        // 사업자등록번호 중복확인 링크 누구나 접근 가능
                        .requestMatchers("/check/business-no").permitAll()
                        // '/my/admin' 경로는 'ADMIN' 역할(Role)을 가진 사용자만 접근 허용
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // '/my/internal' 경로는 'ADMIN', 'PRODUCTION', 'PURCHASING' 역할 중 하나라도 있으면 접근 허용
                        .requestMatchers("/internal/**").hasAnyRole("ADMIN", "PRODUCTION", "PURCHASING")
                        // '/my/external' 경로는 'ADMIN', 'PARTNER' 역할 중 하나라도 있으면 접근 허용
                        .requestMatchers("/external/**").hasAnyRole("ADMIN", "PARTNER")
                        .anyRequest().authenticated()
                )
                .csrf((csrf) -> csrf
                        .ignoringRequestMatchers("/find/**")
                )
                // 로그인 설정 시작
                .formLogin((form) -> form
                        // 커스텀 로그인 페이지 URL 설정 (GET '/login' 호출 시 로그인 화면 보여줌)
                        .loginPage("/login")
                        // 로그인 POST 처리 URL 설정 (POST '/login' 호출 시 인증 시도)
                        .loginProcessingUrl("/login")
                        // 로그인 실패 시 동작
                        .failureHandler(customLoginFailureHandler)
                        // 로그인 성공 시 동작
                        .successHandler(customLoginSuccessHandler)
                        // 로그인 관련 페이지는 누구나 접근할 수 있게 허용
                        .permitAll()
                )
                // 자동 로그인
                .rememberMe(httpSecurityRememberMeConfigurer ->
                        httpSecurityRememberMeConfigurer.key("12345678")
                                .tokenRepository(persistentTokenRepository())
                                .userDetailsService(customUserDetailsService)
                                .tokenValiditySeconds(60 * 60 * 24 * 30)

                )
                .sessionManagement(session -> session
                        // 로그인 성공 시 세션 ID 새로 발급
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
                        // 세션 만료 시 이동
                        .invalidSessionUrl("/intro")
                        // 동시 로그인 허용 개수 1개로 제한
                        .maximumSessions(1)
                        // 추가 로그인 허용 (기존 세션 끊김)
                        .maxSessionsPreventsLogin(false)
                );

        // 설정 완료 후 SecurityFilterChain 객체를 반환
        return httpSecurity.build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }
}
