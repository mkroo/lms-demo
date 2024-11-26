package com.mkroo.lmsdemo.config

import com.mkroo.lmsdemo.domain.Authority
import com.mkroo.lmsdemo.security.AccountJwtAuthenticationProvider
import com.mkroo.lmsdemo.security.JwtUtils
import com.mkroo.lmsdemo.security.JwtAuthenticationFilter
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
class SecurityConfig {
    companion object {
        private val LOGIN_PATH = AntPathRequestMatcher("/login", "POST")
        private val REGISTER_PATH = AntPathRequestMatcher("/register", "POST")
    }

    @Bean
    fun jwtUtils() : JwtUtils {
        return JwtUtils("E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855", Duration.ofDays(7))
    }

    @Bean
    fun accountJwtAuthenticationProvider(jwtUtils: JwtUtils) : AccountJwtAuthenticationProvider {
        return AccountJwtAuthenticationProvider(jwtUtils)
    }

    @Bean
    fun filterChain(http: HttpSecurity, authenticationProvider: AccountJwtAuthenticationProvider) : SecurityFilterChain {
        return http
            .authorizeHttpRequests {
                it
                    .requestMatchers(LOGIN_PATH, REGISTER_PATH).permitAll()
                    .requestMatchers(AntPathRequestMatcher("/lectures", "POST")).hasAuthority(Authority.OPEN_LECTURE.name)
                    .anyRequest().authenticated()
            }
            .formLogin(FormLoginConfigurer<HttpSecurity>::disable)
            .csrf(CsrfConfigurer<HttpSecurity>::disable)
            .addFilterAt(
                JwtAuthenticationFilter(
                    RequestMatchers.not(anyOf(LOGIN_PATH, REGISTER_PATH)),
                    authenticationProvider
                ),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .build()
    }
}