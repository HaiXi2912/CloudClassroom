package com.cloudclassroom.config;

import com.cloudclassroom.security.JwtAuthenticationFilter;
import com.cloudclassroom.security.JwtTokenService;
import com.cloudclassroom.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.nio.charset.StandardCharsets;

/**
 * Spring Security 配置：
 * - 关闭 Session（纯 JWT）
 * - 放行登录/注册与 Swagger
 */
@Configuration
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenService jwtTokenService, ObjectMapper objectMapper) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .headers(headers -> headers
        .contentTypeOptions(Customizer.withDefaults())
        .frameOptions(frame -> frame.sameOrigin())
        .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
      )
        .authorizeRequests(auth -> auth
            .antMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
          .antMatchers(HttpMethod.GET, "/api/files/**").permitAll()
            .antMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(eh -> eh
            .authenticationEntryPoint((request, response, ex) -> {
              response.setStatus(200);
              response.setCharacterEncoding(StandardCharsets.UTF_8.name());
              response.setContentType("application/json;charset=UTF-8");
              response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(40100, "未登录")));
            })
            .accessDeniedHandler((request, response, ex) -> {
              response.setStatus(200);
              response.setCharacterEncoding(StandardCharsets.UTF_8.name());
              response.setContentType("application/json;charset=UTF-8");
              response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(40300, "无权限")));
            })
        )
        .httpBasic(Customizer.withDefaults());

    http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
