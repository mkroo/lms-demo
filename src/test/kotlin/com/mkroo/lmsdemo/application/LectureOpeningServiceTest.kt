package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.AccountRepository
import com.mkroo.lmsdemo.dao.LectureRepository
import com.mkroo.lmsdemo.dao.TeacherRepository
import com.mkroo.lmsdemo.domain.Student
import com.mkroo.lmsdemo.domain.Teacher
import com.mkroo.lmsdemo.dto.LectureOpeningRequest
import com.mkroo.lmsdemo.exception.IllegalAuthenticationException
import com.mkroo.lmsdemo.helper.Fixture
import com.mkroo.lmsdemo.security.AccountJwtAuthentication
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class LectureOpeningServiceTest(
    private val lectureRepository: LectureRepository,
    private val teacherRepository: TeacherRepository,
    private val accountRepository: AccountRepository,
) : BehaviorSpec({
    Given("강의 개설을 시도했을 때") {
        val lectureOpeningService = LectureOpeningService(
            lectureRepository = lectureRepository,
            teacherRepository = teacherRepository
        )

        val request = LectureOpeningRequest(
            title = "강의 제목",
            maxStudentCount = 10,
            price = 100000
        )

        When("강사가 개설을 시도하면") {
            val account = accountRepository.save(Fixture.sample<Teacher>())
            val authentication = AccountJwtAuthentication(account.id, account.authorities)

            Then("강의가 개설된다") {
                val openedLecture = lectureOpeningService.openLecture(authentication, request)

                val savedLecture = lectureRepository.findById(openedLecture.id)

                savedLecture?.title shouldBe request.title
                savedLecture?.maxStudentCount shouldBe request.maxStudentCount
                savedLecture?.price shouldBe request.price
                savedLecture?.teacher shouldBe account
            }

            Then("개설된 강의를 반환한다") {
                val openedLecture = lectureOpeningService.openLecture(authentication, request)

                openedLecture.title shouldBe request.title
                openedLecture.maxStudentCount shouldBe request.maxStudentCount
                openedLecture.price shouldBe request.price
                openedLecture.teacher shouldBe account
            }
        }

        When("학생이 개설을 시도하면") {
            val account = accountRepository.save(Fixture.sample<Student>())
            val authentication = AccountJwtAuthentication(account.id, account.authorities)

            Then("오류가 발생한다") {
                shouldThrow<IllegalAuthenticationException> { lectureOpeningService.openLecture(authentication, request) }
            }
        }
    }
})
