package com.mkroo.lmsdemo.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.mkroo.lmsdemo.dao.AccountRepository
import com.mkroo.lmsdemo.domain.Student
import com.mkroo.lmsdemo.domain.Teacher
import com.mkroo.lmsdemo.helper.Fixture
import com.mkroo.lmsdemo.security.JwtUtils
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class LectureControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val accountRepository: AccountRepository,
    private val jwtUtils: JwtUtils
) : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    Given("강의 개설을 할 때") {
        val requestBuilder = post("/lectures").contentType(MediaType.APPLICATION_JSON)
        val request = mapOf(
            "title" to "강의 제목",
            "maxStudentCount" to 10,
            "price" to 100000
        )

        When("인증 정보가 없으면") {
            Then("401 오류를 반환한다") {
                mockMvc
                    .perform(
                        requestBuilder
                            .content(objectMapper.writeValueAsString(request))
                            .header("Authorization", "")
                    )
                    .andExpect(
                        status().isUnauthorized
                    )
            }
        }

        When("학생이 개설을 시도하면") {
            val studentAccount = accountRepository.save(Fixture.sample<Student>())
            val token = jwtUtils.issue(studentAccount)

            Then("403 오류를 반환한다") {
                mockMvc
                    .perform(
                        requestBuilder
                            .content(objectMapper.writeValueAsString(request))
                            .header("Authorization", "Bearer $token")
                    )
                    .andExpect(
                        status().isForbidden
                    )
            }
        }

        When("강사가 개설을 시도하면") {
            val teacherAccount = accountRepository.save(Fixture.sample<Teacher>())
            val token = jwtUtils.issue(teacherAccount)

            Then("강의가 개설된다") {
                mockMvc
                    .perform(
                        requestBuilder
                            .content(objectMapper.writeValueAsString(request))
                            .header("Authorization", "Bearer $token")
                    )
                    .andExpect(
                        status().isNoContent
                    )
            }
        }
    }
})
