package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.AccountRepository
import com.mkroo.lmsdemo.domain.PasswordValidator
import com.mkroo.lmsdemo.domain.Student
import com.mkroo.lmsdemo.domain.Teacher
import com.mkroo.lmsdemo.dto.RegisterUserRequest
import com.mkroo.lmsdemo.exception.UserRegistrationFieldException
import com.mkroo.lmsdemo.helper.Fixture
import com.navercorp.fixturemonkey.kotlin.setExp
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.security.crypto.password.PasswordEncoder

@DataJpaTest
class AuthServiceTest(
    private val accountRepository: AccountRepository,
) : BehaviorSpec({
    Given("회원가입을 할 때") {
        val passwordEncoder: PasswordEncoder = mockk()
        val passwordValidator: PasswordValidator = mockk()

        val authService = AuthService(
            accountRepository = accountRepository,
            passwordEncoder = passwordEncoder,
            passwordValidator = passwordValidator
        )

        every { passwordValidator.isValid(any()) } returns true
        every { passwordEncoder.encode(any()) } returns "encodedPassword"

        When("학생 역할로 생성하면") {
            val studentRequest: RegisterUserRequest = Fixture
                .getBuilder<RegisterUserRequest>()
                .setExp(RegisterUserRequest::role, "student")
                .sample()

            Then("학생 계정이 생성된다") {
                authService.register(studentRequest)

                val account = accountRepository.findByEmail(studentRequest.email)
                (account is Student) shouldBe true
            }
        }

        When("강사 역할로 생성하면") {
            val teacherRequest = Fixture
                .getBuilder<RegisterUserRequest>()
                .setExp(RegisterUserRequest::role, "teacher")
                .sample()

            Then("강사 계정이 생성된다") {
                authService.register(teacherRequest)

                val account = accountRepository.findByEmail(teacherRequest.email)
                (account is Teacher) shouldBe true
            }
        }

        When("이메일이 중복되었다면") {
            val duplicateEmailRequestBuilder = Fixture
                .getBuilder<RegisterUserRequest>()
                .setExp(RegisterUserRequest::email, Fixture.getEmail())

            authService.register(duplicateEmailRequestBuilder.sample())

            Then("회원가입에 실패한다") {
                shouldThrow<UserRegistrationFieldException> { authService.register(duplicateEmailRequestBuilder.sample()) }
            }
        }

        When("핸드폰번호가 중복되었다면") {
            val duplicateEmailRequestBuilder = Fixture
                .getBuilder<RegisterUserRequest>()
                .setExp(RegisterUserRequest::phoneNumber, Fixture.getPhoneNumber())

            authService.register(duplicateEmailRequestBuilder.sample())

            Then("회원가입에 실패한다") {
                shouldThrow<UserRegistrationFieldException> { authService.register(duplicateEmailRequestBuilder.sample()) }
            }
        }

        When("비밀번호의 형식이 올바르지 않다면") {
            val request = Fixture.getBuilder<RegisterUserRequest>().sample()
            every { passwordValidator.isValid(request.password) } returns false

            Then("회원가입에 실패한다") {
                shouldThrow<UserRegistrationFieldException> { authService.register(request) }
            }
        }

        When("평문 비밀번호를 입력하면") {
            val plainPassword = "password123"
            val encodedPassword = "encodedPassword"
            val request = Fixture.getBuilder<RegisterUserRequest>()
                .setExp(RegisterUserRequest::password, plainPassword)
                .sample()

            every { passwordEncoder.encode(plainPassword) } returns encodedPassword

            Then("암호화되어 저장된다") {
                authService.register(request)

                val account = accountRepository.findByEmail(request.email)

                account?.encodedPassword shouldBe encodedPassword
            }
        }
    }
})
