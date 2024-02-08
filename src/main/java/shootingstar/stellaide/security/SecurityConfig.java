package shootingstar.stellaide.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import shootingstar.stellaide.security.jwt.JwtAuthenticationFilter;
import shootingstar.stellaide.security.jwt.JwtTokenProvider;
import shootingstar.stellaide.util.LoginListRedisUtil;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final LoginListRedisUtil loginListRedisUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                                authorize
                                        .requestMatchers("/api/auth/delete/user").authenticated() // 로그아웃은 인증된 사용자만 접근 가능
                                        .requestMatchers( // 인증 없이 접근 허용
                                                "/error",
                                                "/api/verification/**",
                                                "/api/check-duplicate/**",
                                                "/api/auth/**"
                                        ).permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/user/**").permitAll() // GET 요청에 대한 특정 경로 역시 인증 없이 접근 허용
//                                .requestMatchers("/api/user/test").hasRole("USER")
                                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, loginListRedisUtil), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(authenticationManager -> authenticationManager
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}