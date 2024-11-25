package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.UserRepository
import com.mkroo.lmsdemo.domain.User
import com.mkroo.lmsdemo.dto.LoginRequest
import com.mkroo.lmsdemo.dto.LoginResponse
import com.mkroo.lmsdemo.exception.LoginFailureException
import com.mkroo.lmsdemo.helper.Fixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder

class LoginServiceTest : BehaviorSpec({
    Given("로그인을 시도했을때") {
        val userRepository: UserRepository = mockk()
        val passwordEncoder: PasswordEncoder = mockk()
        val authenticationTokenProvider: AuthenticationTokenProvider = mockk()

        val loginService = LoginService(
            userRepository = userRepository,
            passwordEncoder = passwordEncoder,
            authenticationTokenProvider = authenticationTokenProvider
        )

        val request = LoginRequest(
            email = Fixture.getEmail(),
            password = Fixture.getPassword()
        )

        When("이메일에 해당하는 유저가 있고") {
            val user = Fixture.getUser()

            every { userRepository.findByEmail(request.email) } returns user

            When("비밀번호가 일치하면") {
                val token = "anyToken"

                every { passwordEncoder.matches(request.password, user.encodedPassword) } returns true
                every { authenticationTokenProvider.issue(user) } returns token

                Then("유저의 토큰을 발급한다") {
                    loginService.login(request)

                    verify { authenticationTokenProvider.issue(user) }
                }

                Then("토큰을 포함한 로그인 응답을 반환한다") {
                    val response = loginService.login(request)
                    response shouldBe LoginResponse(token)
                }
            }

            When("비밀번호가 틀리면") {
                every { passwordEncoder.matches(request.password, user.encodedPassword) } returns false

                Then("로그인 실패 오류가 발생한다") {
                    shouldThrow<LoginFailureException> { loginService.login(request) }
                }
            }
        }

        When("이메일에 해당하는 유저가 없으면") {
            every { userRepository.findByEmail(request.email) } returns null

            Then("로그인 실패 오류가 발생한다") {
                shouldThrow<LoginFailureException> { loginService.login(request) }
            }
        }
    }
})
