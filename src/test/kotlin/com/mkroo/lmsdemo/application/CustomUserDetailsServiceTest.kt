package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.UserRepository
import com.mkroo.lmsdemo.domain.User
import com.mkroo.lmsdemo.domain.UserRole
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.core.userdetails.UsernameNotFoundException

class CustomUserDetailsServiceTest : BehaviorSpec({
    Given("이메일로 사용자를 조회할 때") {
        val userRepository = mockk<UserRepository>()
        val customUserDetailsService = CustomUserDetailsService(userRepository)

        val email = "test@gmail.com"

        When("사용자가 존재한다면") {
            val user = User(
                name = "홍길동",
                email = email,
                phoneNumber = "010-1234-5678",
                encodedPassword = "encodedPassword",
                role = UserRole.STUDENT
            )
            every { userRepository.findByEmail(email) } returns user

            Then("유저 정보를 반환한다") {
                val userDetails = customUserDetailsService.loadUserByUsername(email)

                userDetails.username shouldBe user.email
                userDetails.password shouldBe user.encodedPassword
            }
        }

        When("사용자가 존재하지 않는다면") {
            every { userRepository.findByEmail(email) } returns null

            Then("로그인 실패 오류를 발생시킨다") {
                shouldThrow<UsernameNotFoundException> { customUserDetailsService.loadUserByUsername(email) }
            }
        }
    }
})
