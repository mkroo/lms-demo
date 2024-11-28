package com.mkroo.lmsdemo.config

import com.mkroo.lmsdemo.domain.Authority
import com.mkroo.lmsdemo.security.AccountJwtAuthenticationProvider
import com.mkroo.lmsdemo.security.JwtUtils
import com.mkroo.lmsdemo.security.JwtAuthenticationFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatchers
import org.springframework.security.web.util.matcher.RequestMatchers.anyOf
import java.time.Duration


@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfig {
    companion object {
        private val LOGIN_PATH = AntPathRequestMatcher("/login", "POST")
        private val REGISTER_PATH = AntPathRequestMatcher("/register", "POST")
    }

    @Bean
    fun jwtUtils(jwtProperties: JwtProperties) : JwtUtils {
        return JwtUtils(jwtProperties.secret, jwtProperties.expiresIn)
    }

    @Bean
    fun filterChain(http: HttpSecurity, jwtUtils: JwtUtils) : SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it
                    .requestMatchers(LOGIN_PATH, REGISTER_PATH).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/lectures", "POST")).hasAuthority(Authority.OPEN_LECTURE.name)
                    .requestMatchers(AntPathRequestMatcher("/lectures", "GET")).hasAuthority(Authority.LIST_LECTURES.name)
                    .requestMatchers(AntPathRequestMatcher("/lecture-applications", "POST")).hasAuthority(Authority.APPLY_LECTURE.name)
                    .anyRequest().denyAll()
            }
            .formLogin(FormLoginConfigurer<HttpSecurity>::disable)
            .csrf(CsrfConfigurer<HttpSecurity>::disable)
            .addFilterAt(
                JwtAuthenticationFilter(
                    RequestMatchers.not(anyOf(LOGIN_PATH, REGISTER_PATH)),
                    AccountJwtAuthenticationProvider(jwtUtils)
                ),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .build()
    }
}