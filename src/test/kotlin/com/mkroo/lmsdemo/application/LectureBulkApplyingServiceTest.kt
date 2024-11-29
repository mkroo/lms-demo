package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.AccountRepository
import com.mkroo.lmsdemo.dao.LectureRepository
import com.mkroo.lmsdemo.domain.Lecture
import com.mkroo.lmsdemo.domain.Student
import com.mkroo.lmsdemo.domain.Teacher
import com.mkroo.lmsdemo.dto.LectureBulkApplyingRequest
import com.mkroo.lmsdemo.dto.LectureBulkApplyingResponse
import com.mkroo.lmsdemo.exception.IllegalAuthenticationException
import com.mkroo.lmsdemo.helper.Fixture
import com.mkroo.lmsdemo.security.AccountJwtAuthentication
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class LectureBulkApplyingServiceTest(
    private val lectureRepository: LectureRepository,
    private val accountRepository: AccountRepository,
) : BehaviorSpec({
    Given("여러개의 강의를 신청할 때") {
        val lectureApplyingService: LectureApplyingService = mockk()
        val lectureBulkApplyingService = LectureBulkApplyingService(
            lectureApplyingService = lectureApplyingService,
            lectureRepository = lectureRepository,
            accountRepository = accountRepository
        )

        val student = accountRepository.save(Fixture.sample<Student>())
        val lectures = List(3) {
            Lecture(
                title = "lecture $it",
                teacher = accountRepository.save(Fixture.sample<Teacher>()) as Teacher,
                maxStudentCount = 10,
                price = 10000,
            ).let(lectureRepository::save)
        }
        val authentication = AccountJwtAuthentication(student.id, student.authorities)

        When("올바르지 않은 인증인 경우") {
            val invalidAuth = AccountJwtAuthentication(0L, emptySet())
            val request = LectureBulkApplyingRequest(listOf(1L, 2L, 3L))

            Then("오류가 발생한다") {
                shouldThrow<IllegalAuthenticationException> { lectureBulkApplyingService.applyLectures(invalidAuth, request) }
            }
        }

        When("ID에 해당하는 강의가 없는 경우") {
            val invalidLectureId = 0L
            val request = LectureBulkApplyingRequest(listOf(invalidLectureId))
            val results = lectureBulkApplyingService.applyLectures(authentication, request)

            Then("실패 이유와 함께 신청 실패한 강의 ID 값을 반환한다") {
                results.failedLectures shouldBe listOf(
                    LectureBulkApplyingResponse.FailedLecture(invalidLectureId, "강의를 찾을 수 없습니다.")
                )
            }
        }

        When("일부만 신청 성공하는 경우") {
            val lectureApplyException = IllegalStateException("Some weird exception")

            every { lectureApplyingService.applyLecture(student, lectures[0]) } returns lectures[0]
            every { lectureApplyingService.applyLecture(student, lectures[1]) } returns lectures[1]
            every { lectureApplyingService.applyLecture(student, lectures[2]) } throws lectureApplyException

            val request = LectureBulkApplyingRequest(
                listOf(lectures[0].id, lectures[1].id, lectures[2].id)
            )
            val results = lectureBulkApplyingService.applyLectures(authentication, request)

            Then("성공한 강의 ID를 반환한다") {
                results.appliedLectureIds shouldBe listOf(lectures[0].id, lectures[1].id)
            }

            Then("개별 강의 신청오류의 메세지를 이유로 반환한다") {
                results.failedLectures shouldBe listOf(
                    LectureBulkApplyingResponse.FailedLecture(lectures[2].id, lectureApplyException.localizedMessage),
                )
            }
        }
    }
})
