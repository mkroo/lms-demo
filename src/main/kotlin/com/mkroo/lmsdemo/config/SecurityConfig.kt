package com.mkroo.lmsdemo.config

import com.mkroo.lmsdemo.application.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun filterChain(http: HttpSecurity, customUserDetailsService: CustomUserDetailsService) : SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/register", "/login").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin(FormLoginConfigurer<HttpSecurity>::disable)
            .csrf(CsrfConfigurer<HttpSecurity>::disable)
            .userDetailsService(customUserDetailsService)
            .build()
    }
}