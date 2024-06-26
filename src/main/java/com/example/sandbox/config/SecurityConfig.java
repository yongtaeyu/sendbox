package com.example.sandbox.config;


import com.example.sandbox.jwt.JwtAuthenticationFilter;
import com.example.sandbox.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final AccessDeniedHandlerImpl accessDeniedHandler;

    private final AuthenticationEntryPointImpl authenticationEntryPointImpl;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(SessionManagementConfigurer->SessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((request)->
                        request.requestMatchers("/members/sign-in").permitAll()
                                    .requestMatchers("/members/excel", "/members/excelDownload").permitAll()
                                .requestMatchers("/members/test").hasRole("USER")
                                .anyRequest()
                                .authenticated()
                ).addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ExceptionHandlingConfigurer->ExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPointImpl)
                )
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
