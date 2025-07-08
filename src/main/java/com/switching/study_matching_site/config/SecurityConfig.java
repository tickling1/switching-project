package com.switching.study_matching_site.config;

import com.switching.study_matching_site.jwt.CustomLogoutFilter;
import com.switching.study_matching_site.jwt.JWTFilter;
import com.switching.study_matching_site.jwt.JWTUtil;
import com.switching.study_matching_site.repository.RefreshRepository;
import com.switching.study_matching_site.service.CustomerUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private RefreshRepository refreshRepository;
    private final CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil,
                          RefreshRepository refreshRepository, CustomerUserDetailsService customerUserDetailsService) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.customerUserDetailsService = customerUserDetailsService;
    }

    // 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //csrf disable
        /**
         * csrf 를 disable 설정
         * 세션 방식에서는 세션이 항상 고정되기 떄문에 csrf 공격을 방어해줘야하지만
         * JWT 방식은 세션을 STATELESS 상태로 관리하기 떄문에 csrf 공격을 방어하지 않아도 됨
         */
        http
                .csrf((auth) -> auth.disable());

        /**
         * JWT 방식을 사용해서 로그인을 진행할 것이기 때문에 From 방식과, basic 방식을 disalble
         */
        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());


        //경로별 인가 작업
        // '/'은 바로 swagger ui 화면을 넘어갈 때 필요
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(
                                "/" ,"/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**",
                                "/login","/reissue", "/rooms/**", "/rooms/list",
                                "/", "/members/**", "/actuator/health").permitAll()// 해당 경로에서는 모든 권한를 허용함
                        .anyRequest().authenticated()); // 나머지 요청에서는 로그인한 사람만 들어갈 수 있음.


        // 필터 등록 UsernamePasswordAuthenticationFilter 을 대신할 필터 등록
        // 필터 추가 LoginFilter()는 인자를 받음
        // (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
        http
                .addFilterBefore(new JWTFilter(jwtUtil, customerUserDetailsService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        
        //세션을 STATELESS 상태로 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
