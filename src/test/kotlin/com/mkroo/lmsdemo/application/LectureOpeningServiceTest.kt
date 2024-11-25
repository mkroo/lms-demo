package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.LectureRepository
import com.mkroo.lmsdemo.domain.Lecture
import com.mkroo.lmsdemo.dto.LectureOpeningRequest
import com.mkroo.lmsdemo.exception.NotPermittedException
import com.mkroo.lmsdemo.helper.Fixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

class LectureOpeningServiceTest : BehaviorSpec({
    Given("강의 개설을 시도했을 때") {
        val lectureRepository: LectureRepository = mockk(relaxed = true)
        val lectureOpeningService = LectureOpeningService(
            lectureRepository = lectureRepository
        )

        val request = LectureOpeningRequest(
            title = "강의 제목",
            maxStudentCount = 10,
            price = 100000
        )

        When("강사가 개설을 시도하면") {
            val user = Fixture.getTeacher()

            val lecture: Lecture = mockk()
            every { lectureRepository.save(any()) } returns lecture

            Then("강의가 개설된다") {
                lectureOpeningService.openLecture(user, request)

                val slot = slot<Lecture>()
                verify { lectureRepository.save(capture(slot)) }

                slot.captured.title shouldBe request.title
                slot.captured.maxStudentCount shouldBe request.maxStudentCount
                slot.captured.price shouldBe request.price
                slot.captured.teacher shouldBe user
            }

            Then("개설된 강의를 반환한다") {
                val openedLecture = lectureOpeningService.openLecture(user, request)

                openedLecture shouldBe lecture
            }
        }

        When("학생이 개설을 시도하면") {
            val user = Fixture.getStudent()

            Then("오류가 발생한다") {
                shouldThrow<NotPermittedException> { lectureOpeningService.openLecture(user, request) }
            }
        }
    }
})
