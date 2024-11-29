package com.mkroo.lmsdemo.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.mkroo.lmsdemo.dao.AccountRepository
import com.mkroo.lmsdemo.dao.LectureRepository
import com.mkroo.lmsdemo.domain.Lecture
import com.mkroo.lmsdemo.domain.Student
import com.mkroo.lmsdemo.domain.Teacher
import com.mkroo.lmsdemo.helper.Fixture
import com.mkroo.lmsdemo.security.JwtUtils
import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LectureControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val accountRepository: AccountRepository,
    private val lectureRepository: LectureRepository,
    private val jwtUtils: JwtUtils
) : BehaviorSpec({
    fun createAnonymousToken() : String = ""
    fun createStudentToken() : String = accountRepository.save(Fixture.sample<Student>()).let(jwtUtils::issue)
    fun createTeacherToken() : String = accountRepository.save(Fixture.sample<Teacher>()).let(jwtUtils::issue)
    fun createLectures(count: Int) : List<Lecture> {
        val teacher = accountRepository.save(Fixture.sample<Teacher>()) as Teacher

        return List(count) {
            lectureRepository.save(
                Lecture(
                    title = "강의 제목 $it",
                    maxStudentCount = 10,
                    price = 100000,
                    teacher = teacher
                )
            )
        }
    }

    val lectures = createLectures(5)
    val anonymousToken = createAnonymousToken()
    val studentToken = createStudentToken()
    val teacherToken = createTeacherToken()

    Given("강의 개설을 할 때") {
        fun lectureOpeningRequest(token: String) : MockHttpServletRequestBuilder {
            val request = mapOf(
                "title" to "강의 제목",
                "maxStudentCount" to 10,
                "price" to 100000
            )

            return post("/lectures")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer $token")
        }

        When("인증 정보가 없으면") {
            Then("401 오류를 반환한다") {
                mockMvc
                    .perform(lectureOpeningRequest(anonymousToken))
                    .andExpect(
                        status().isUnauthorized
                    )
            }
        }

        When("학생이 개설을 시도하면") {
            Then("403 오류를 반환한다") {
                mockMvc
                    .perform(lectureOpeningRequest(studentToken))
                    .andExpect(
                        status().isForbidden
                    )
            }
        }

        When("강사가 개설을 시도하면") {
            Then("강의가 개설된다") {
                mockMvc
                    .perform(lectureOpeningRequest(teacherToken))
                    .andExpect(
                        status().isOk
                    )
            }
        }
    }

    Given("강의를 조회할 때") {
        fun lectureListingRequest(token: String) : MockHttpServletRequestBuilder {
            return get("/lectures")
                .header("Authorization", "Bearer $token")
        }

        When("인증 정보가 없으면") {
            Then("401 오류를 반환한다") {
                mockMvc
                    .perform(lectureListingRequest(anonymousToken))
                    .andExpect(
                        status().isUnauthorized
                    )
            }
        }

        When("학생이 조회하면") {
            Then("강의 목록을 반환한다") {
                mockMvc
                    .perform(lectureListingRequest(studentToken))
                    .andExpect(
                        status().isOk
                    )
            }
        }

        When("강사가 조회하면") {
            Then("강의 목록을 반환한다") {
                mockMvc
                    .perform(lectureListingRequest(teacherToken))
                    .andExpectAll(
                        status().isOk,
                        jsonPath("$.status").value("success"),
                        jsonPath("$.data.items").isArray(),
                        jsonPath("$.data.items[0].id").isNumber(),
                        jsonPath("$.data.items[0].title").isString(),
                        jsonPath("$.data.items[0].price").isNumber(),
                        jsonPath("$.data.items[0].teacherName").isString(),
                        jsonPath("$.data.items[0].currentStudentCount").isNumber(),
                        jsonPath("$.data.items[0].maxStudentCount").isNumber(),
                        jsonPath("$.data.items[0].createdAt").isString(),
                        jsonPath("$.data.page").isNumber(),
                        jsonPath("$.data.size").isNumber(),
                        jsonPath("$.data.totalItems").isNumber(),
                    )
            }
        }
    }

    Given("올바른 강의들을 신청할 때") {
        fun lectureApplyingRequest(token: String, lectures: List<Lecture>) : MockHttpServletRequestBuilder {
            val request = mapOf(
                "lectureIds" to lectures.map { it.id }
            )

            return post("/lecture-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer $token")
        }

        When("인증 정보가 없으면") {
            Then("401 오류를 반환한다") {
                mockMvc
                    .perform(lectureApplyingRequest(anonymousToken, lectures))
                    .andExpect(
                        status().isUnauthorized
                    )
            }
        }

        When("학생이 신청하면") {
            Then("강의 목록을 반환한다") {
                mockMvc
                    .perform(lectureApplyingRequest(studentToken, lectures))
                    .andExpect(
                        status().isOk
                    )
            }
        }

        When("강사가 신청하면") {
            Then("강의 목록을 반환한다") {
                mockMvc
                    .perform(lectureApplyingRequest(teacherToken, lectures))
                    .andExpect(
                        status().isOk
                    )
                    .andExpectAll(
                        status().isOk,
                        jsonPath("$.status").value("success"),
                        jsonPath("$.data.appliedLectureIds").isArray(),
                        jsonPath("$.data.appliedLectureIds[0]").value(lectures[0].id),
                        jsonPath("$.data.appliedLectureIds[1]").value(lectures[1].id),
                        jsonPath("$.data.appliedLectureIds[2]").value(lectures[2].id),
                        jsonPath("$.data.failedLectures").isArray(),
                        jsonPath("$.data.failedLectures").isEmpty()
                    )
            }
        }
    }
})
