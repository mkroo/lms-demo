package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.AccountRepository
import com.mkroo.lmsdemo.dao.LectureRepository
import com.mkroo.lmsdemo.domain.Lecture
import com.mkroo.lmsdemo.domain.Student
import com.mkroo.lmsdemo.domain.Teacher
import com.mkroo.lmsdemo.helper.Fixture
import com.mkroo.lmsdemo.infrastructure.lockclient.InMemoryLockClient
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class LectureApplyingServiceTest(
    private val accountRepository: AccountRepository,
    private val lectureRepository: LectureRepository,
) : BehaviorSpec({
    Given("강의를 신청할 때") {
        val lockClient = InMemoryLockClient()
        val lectureApplyingService = LectureApplyingService(lectureRepository = lectureRepository, lockClient = lockClient)
        val maxStudentCount = 10

        val teacher = accountRepository.save(Fixture.sample<Teacher>()) as Teacher
        val student = accountRepository.save(Fixture.sample<Student>())
        val lecture = lectureRepository.save(
            Lecture(
                title = "favorite lecture",
                teacher = teacher,
                maxStudentCount = maxStudentCount,
                price = 100000,
            )
        )

        When("강의가 이미 수강인원이 가득 찼다면") {
            val otherStudents = List(maxStudentCount) { accountRepository.save(Fixture.sample<Student>()) }
            otherStudents.forEach { lecture.apply(it) }

            Then("강의 신청에 실패한다") {
                shouldThrow<IllegalStateException> { lectureApplyingService.applyLecture(student, lecture) }
            }
        }

        When("수강인원 내에서 강의를 신청하는 경우") {
            val students = List(maxStudentCount - 1) { accountRepository.save(Fixture.sample<Student>()) }
            students.forEach { lecture.apply(it) }

            Then("강의 신청에 성공한다") {
                val applicationBeforeApply = lecture.applicationCount
                val appliedLecture = lectureApplyingService.applyLecture(student, lecture)

                appliedLecture shouldBe lecture
                appliedLecture.applicationCount shouldBe applicationBeforeApply + 1
            }
        }
    }
})
