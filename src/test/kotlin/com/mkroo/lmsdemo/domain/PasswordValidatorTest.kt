package com.mkroo.lmsdemo.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class PasswordValidatorTest : BehaviorSpec({
    Given("비밀번호를 길이를 검증할 때") {
        val passwordValidator = PasswordValidator()

        When("길이가 6~10자리 사이라면") {
            val password ="abcde12345"

            Then("검증에 성공한다") {
                passwordValidator.isValid(password) shouldBe true
            }
        }

        When("길이가 6자리 미만이라면") {
            val password ="apple"

            Then("검증에 실패한다") {
                passwordValidator.isValid(password) shouldBe false
            }
        }

        When("길이가 10자리를 초과한다면") {
            val password ="apple123456"

            Then("검증에 실패한다") {
                passwordValidator.isValid(password) shouldBe false
            }
        }
    }

    Given("비밀번호에 사용된 문자를 검증할 때") {
        val passwordValidator = PasswordValidator()


        When("숫자로만 구성되었다면") {
            val password = "123456"

            Then("검증에 실패한다") {
                passwordValidator.isValid(password) shouldBe false
            }
        }

        When("영문 소문자로만 구성되었다면") {
            val password = "banana"

            Then("검증에 실패한다") {
                passwordValidator.isValid(password) shouldBe false
            }
        }

        When("영문 대문자로만 구성되었다면") {
            val password = "BANANA"

            Then("검증에 실패한다") {
                passwordValidator.isValid(password) shouldBe false
            }
        }

        When("숫자와 영문 소문자로만 구성되었다면") {
            val password = "123abc"

            Then("검증에 성공한다") {
                passwordValidator.isValid(password) shouldBe true
            }
        }

        When("숫자와 영문 대문자로만 구성되었다면") {
            val password = "123ABC"

            Then("검증에 성공한다") {
                passwordValidator.isValid(password) shouldBe true
            }
        }

        When("영문 소문자와 영문 대문자로만 구성되었다면") {
            val password = "abcABC"

            Then("검증에 성공한다") {
                passwordValidator.isValid(password) shouldBe true
            }
        }

        When("숫자와 영문 소문자와 영문 대문자로 구성되었다면") {
            val password = "abcABC123"

            Then("검증에 성공한다") {
                passwordValidator.isValid(password) shouldBe true
            }
        }
    }
})
