package com.mkroo.lmsdemo.api

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) : BehaviorSpec({
    Given("회원가입을 할 때") {
        val requestBuilder = post("/register").contentType(MediaType.APPLICATION_JSON)

        When("모든 정보가 올바르게 입력되었다면") {
            val request = mapOf(
                "name" to "John Doe",
                "email" to "test@gmail.com",
                "phoneNumber" to "010-1234-5678",
                "password" to "apple123",
                "role" to "student"
            )

            Then("회원가입에 성공한다") {
                mockMvc
                    .perform(requestBuilder.content(objectMapper.writeValueAsString(request)))
                    .andExpect(
                        status().isNoContent
                    )
            }
        }

        When("비밀번호가 유효하지 않다면") {
            val request = mapOf(
                "name" to "John Doe",
                "email" to "test@gmail.com",
                "phoneNumber" to "010-1234-5678",
                "password" to "apple",
                "role" to "student"
            )

            Then("회원가입에 실패한다") {
                mockMvc
                    .perform(requestBuilder.content(objectMapper.writeValueAsString(request)))
                    .andExpect(
                        status().isBadRequest
                    )
            }
        }
    }

    Given("로그인을 할 때") {
        val requestBuilder = post("/login").contentType(MediaType.APPLICATION_JSON)
        val userRegisterRequest = mapOf(
            "name" to "John Doe",
            "email" to "test@gmail.com",
            "phoneNumber" to "010-1234-5678",
            "password" to "apple123",
            "role" to "student"
        )

        mockMvc.perform(
            post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegisterRequest))
        )

        When("이메일과 비밀번호가 올바르게 입력되었다면") {
            val request = mapOf(
                "email" to userRegisterRequest["email"],
                "password" to userRegisterRequest["password"],
            )

            Then("로그인에 성공한다") {
                mockMvc
                    .perform(requestBuilder.content(objectMapper.writeValueAsString(request)))
                    .andExpect(
                        status().isOk
                    )
                    .andExpect(jsonPath("$.token").exists())
            }
        }

        When("이메일이 올바르지 않다면") {
            val request = mapOf(
                "email" to userRegisterRequest["email"] + "invalid",
                "password" to userRegisterRequest["password"],
            )

            Then("로그인에 실패한다") {
                mockMvc
                    .perform(requestBuilder.content(objectMapper.writeValueAsString(request)))
                    .andExpect(
                        status().isBadRequest
                    )
            }
        }

        When("비밀번호가 올바르지 않다면") {
            val request = mapOf(
                "email" to userRegisterRequest["email"],
                "password" to userRegisterRequest["password"] + "invalid",
            )

            Then("로그인에 실패한다") {
                mockMvc
                    .perform(requestBuilder.content(objectMapper.writeValueAsString(request)))
                    .andExpect(
                        status().isBadRequest
                    )
            }
        }
    }
})
