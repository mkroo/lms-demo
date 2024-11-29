package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.AccountRepository
import com.mkroo.lmsdemo.domain.PasswordValidator
import com.mkroo.lmsdemo.domain.Student
import com.mkroo.lmsdemo.domain.Teacher
import com.mkroo.lmsdemo.dto.RegisterUserRequest
import com.mkroo.lmsdemo.exception.UserRegistrationFieldException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val accountRepository: AccountRepository,
    private val passwordEncoder: PasswordEncoder,
    private val passwordValidator: PasswordValidator
) {
    @Transactional
    fun register(request: RegisterUserRequest) {
        request
            .apply(::checkDuplicateEmail)
            .apply(::checkDuplicatePhoneNumber)
            .apply(::checkPasswordConstraints)

        val encodedPassword = passwordEncoder.encode(request.password)

        val account = when (request.role) {
            "student" -> Student(
                email = request.email,
                encodedPassword = encodedPassword,
                name = request.name,
                phoneNumber = request.phoneNumber
            )
            "teacher" -> Teacher(
                email = request.email,
                encodedPassword = encodedPassword,
                name = request.name,
                phoneNumber = request.phoneNumber
            )
            else -> throw UserRegistrationFieldException(RegisterUserRequest::role, "학생(student), 강사(teacher) 중 하나의 역할을 선택해주세요.")
        }

        accountRepository.save(account)
    }

    private fun checkDuplicateEmail(request: RegisterUserRequest) {
        accountRepository.existsByEmail(request.email).takeIf { it }?.let {
            throw UserRegistrationFieldException(RegisterUserRequest::email, "이미 가입된 이메일 주소입니다.")
        }
    }

    private fun checkDuplicatePhoneNumber(request: RegisterUserRequest) {
        accountRepository.existsByPhoneNumber(request.phoneNumber).takeIf { it }?.let {
            throw UserRegistrationFieldException(RegisterUserRequest::phoneNumber, "이미 가입된 휴대폰 번호입니다.")
        }
    }

    private fun checkPasswordConstraints(request: RegisterUserRequest) {
        passwordValidator.isValid(request.password).takeIf { !it }?.let {
            throw UserRegistrationFieldException(RegisterUserRequest::password, "비밀번호는 6~10글자의 영문 대소문자, 숫자로 구성되어야 합니다.")
        }
    }
}