package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.config.PasswordEncoderConfig
import com.mkroo.lmsdemo.dao.AccountRepository
import com.mkroo.lmsdemo.domain.Account
import com.mkroo.lmsdemo.domain.Teacher
import com.mkroo.lmsdemo.dto.LoginRequest
import com.mkroo.lmsdemo.dto.LoginResponse
import com.mkroo.lmsdemo.exception.LoginFailureException
import com.mkroo.lmsdemo.helper.Fixture
import com.mkroo.lmsdemo.security.JwtUtils
import com.navercorp.fixturemonkey.kotlin.setExp
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.password.PasswordEncoder

@DataJpaTest
@Import(PasswordEncoderConfig::class)
class LoginServiceTest(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder
) : BehaviorSpec({
    Given("로그인을 시도했을때") {
        val jwtUtils: JwtUtils = mockk()

        val loginService = LoginService(
            accountRepository = accountRepository,
            passwordEncoder = passwordEncoder,
            jwtUtils = jwtUtils
        )

        When("이메일에 해당하는 계정이 있고") {
            val plainPassword = Fixture.getPassword()
            val encodedPassword = passwordEncoder.encode(plainPassword)

            val account: Account = Fixture.getBuilder<Teacher>()
                .setExp(Teacher::encodedPassword, encodedPassword)
                .sample()

            accountRepository.save(account)

            When("비밀번호가 일치하면") {
                val request = LoginRequest(
                    email = account.email,
                    password = plainPassword
                )
                val token = "anyToken"

                every { jwtUtils.issue(account) } returns token

                Then("계정의 토큰을 발급한다") {
                    loginService.login(request)

                    verify { jwtUtils.issue(account) }
                }

                Then("토큰을 포함한 로그인 응답을 반환한다") {
                    val response = loginService.login(request)
                    response shouldBe LoginResponse(token)
                }
            }

            When("비밀번호가 틀리면") {
                val request = LoginRequest(
                    email = account.email,
                    password = "${plainPassword}!#"
                )

                Then("로그인 실패 오류가 발생한다") {
                    shouldThrow<LoginFailureException> { loginService.login(request) }
                }
            }
        }

        When("이메일에 해당하는 계정이 없으면") {
            val request = LoginRequest(
                email = Fixture.getEmail(),
                password = Fixture.getPassword()
            )

            Then("로그인 실패 오류가 발생한다") {
                shouldThrow<LoginFailureException> { loginService.login(request) }
            }
        }
    }
})
