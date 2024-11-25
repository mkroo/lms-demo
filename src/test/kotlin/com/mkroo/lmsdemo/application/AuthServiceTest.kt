package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.UserRepository
import com.mkroo.lmsdemo.domain.PasswordValidator
import com.mkroo.lmsdemo.domain.UserType
import com.mkroo.lmsdemo.dto.RegisterUserRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder

class AuthServiceTest : BehaviorSpec({
    Given("회원가입을 할 때") {
        val userRepository = mockk<UserRepository>()
        val passwordEncoder = mockk<PasswordEncoder>()
        val passwordValidator = mockk<PasswordValidator>()

        val authService = AuthService(
            userRepository = userRepository,
            passwordEncoder = passwordEncoder,
            passwordValidator = passwordValidator
        )

        val request = RegisterUserRequest(
            name = "홍길동",
            email = "test@gmail.com",
            phoneNumber = "010-1234-5678",
            password = "password123",
            userType = UserType.STUDENT
        )

        every { userRepository.existsByEmail(request.email) } returns false
        every { userRepository.existsByPhoneNumber(request.phoneNumber) } returns false
        every { passwordValidator.isValid(request.password) } returns true
        every { passwordEncoder.encode(request.password) } returns "encodedPassword"
        every { userRepository.save(any()) } returns mockk()

        Then("회원가입에 성공한다") {
            authService.register(request)

            verify { userRepository.save(any()) }
        }

        When("이메일이 중복되었다면") {
            every { userRepository.existsByEmail(request.email) } returns true

            Then("회원가입에 실패한다") {
                shouldThrow<IllegalArgumentException> { authService.register(request) }
            }
        }

        When("핸드폰번호가 중복되었다면") {
            every { userRepository.existsByPhoneNumber(request.phoneNumber) } returns true

            Then("회원가입에 실패한다") {
                shouldThrow<IllegalArgumentException> { authService.register(request) }
            }
        }

        When("비밀번호의 형식이 올바르지 않다면") {
            every { passwordValidator.isValid(request.password) } returns false

            Then("회원가입에 실패한다") {
                shouldThrow<IllegalArgumentException> { authService.register(request) }
            }
        }
    }

    Given("암호화되지 않은 비밀번호를 전달받았을 때") {
        val userRepository = mockk<UserRepository>()
        val passwordEncoder = spyk<PasswordEncoder>()
        val passwordValidator = mockk<PasswordValidator>()

        val authService = AuthService(
            userRepository = userRepository,
            passwordEncoder = passwordEncoder,
            passwordValidator = passwordValidator
        )

        val request = RegisterUserRequest(
            name = "홍길동",
            email = "test@gmail.com",
            phoneNumber = "010-1234-5678",
            password = "password123",
            userType = UserType.STUDENT
        )

        every { userRepository.existsByEmail(request.email) } returns false
        every { userRepository.existsByPhoneNumber(request.phoneNumber) } returns false
        every { passwordValidator.isValid(request.password) } returns true
        every { passwordEncoder.encode(request.password) } returns "encodedPassword"
        every { userRepository.save(any()) } returns mockk()

        Then("비밀번호를 암호화한다") {
            authService.register(request)

            verify { passwordEncoder.encode(request.password) }
        }
    }
})
