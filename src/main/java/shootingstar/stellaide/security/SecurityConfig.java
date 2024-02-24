package shootingstar.stellaide.security;

import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shootingstar.stellaide.security.jwt.JwtAuthenticationFilter;
import shootingstar.stellaide.security.jwt.JwtTokenProvider;
import shootingstar.stellaide.security.jwt.TokenProperty;
import shootingstar.stellaide.util.LoginListRedisUtil;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final LoginListRedisUtil loginListRedisUtil;
    private final TokenProperty tokenProperty;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                                authorize
                                        .requestMatchers(
                                                "/remote/fgt_lang",
                                                "/"
                                        ).denyAll()
                                        .requestMatchers( // 인증 후 접근 허용
                                                "/api/auth/delete/user",
                                                "/api/auth/changePassword",
                                                "/api/auth/checkPassword"
                                        ).authenticated()
                                        .requestMatchers( // 인증 없이 접근 허용
                                                "/error",
                                                "/ws/chat",
                                                "/api/verification/**",
                                                "/api/check-duplicate/**",
                                                "/api/auth/**"
                                        ).permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/user/**").permitAll() // GET 요청에 대한 특정 경로 역시 인증 없이 접근 허용
//                                .requestMatchers("/api/user/test").hasRole("USER")
                                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, loginListRedisUtil, tokenProperty), UsernamePasswordAuthenticationFilter.class)
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 여기에 허용할 오리진 추가
        configuration.setAllowedMethods(Collections.singletonList("*")); // 허용할 HTTP 메소드 설정

        configuration.setAllowCredentials(true); // 쿠키를 넘기기 위해 사용
//        configuration.setMaxAge(3600L); // 브라우저 캐싱 시간(초)

        configuration.setAllowedHeaders(Collections.singletonList("*")); // 허용할 헤더 설정
        configuration.addExposedHeader("Authorization"); // 클라이언트가 접근할 수 있도록 헤더 노출

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 적용
        return source;
    }
}