package com.mkroo.lmsdemo.config

import com.mkroo.lmsdemo.domain.Authority
import com.mkroo.lmsdemo.security.AccountJwtAuthenticationProvider
import com.mkroo.lmsdemo.security.JwtAuthenticationFilter
import com.mkroo.lmsdemo.security.JwtUtils
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
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
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.security.web.util.matcher.RequestMatchers
import org.springframework.security.web.util.matcher.RequestMatchers.anyOf


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
        val permitAllRequestMatcher : RequestMatcher = anyOf(LOGIN_PATH, REGISTER_PATH, PathRequest.toH2Console())

        return http
            .authorizeHttpRequests {
                it
                    .requestMatchers(permitAllRequestMatcher).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/lectures", "POST")).hasAuthority(Authority.OPEN_LECTURE.name)
                    .requestMatchers(AntPathRequestMatcher("/lectures", "GET")).hasAuthority(Authority.LIST_LECTURES.name)
                    .requestMatchers(AntPathRequestMatcher("/lecture-applications", "POST")).hasAuthority(Authority.APPLY_LECTURE.name)
                    .anyRequest().denyAll()
            }
            .formLogin(FormLoginConfigurer<HttpSecurity>::disable)
            .csrf(CsrfConfigurer<HttpSecurity>::disable)
            .addFilterAt(
                JwtAuthenticationFilter(
                    RequestMatchers.not(permitAllRequestMatcher),
                    AccountJwtAuthenticationProvider(jwtUtils)
                ),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .build()
    }
}