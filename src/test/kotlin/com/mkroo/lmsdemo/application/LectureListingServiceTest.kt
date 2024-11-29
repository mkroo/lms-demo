package com.mkroo.lmsdemo.application

import com.mkroo.lmsdemo.dao.AccountRepository
import com.mkroo.lmsdemo.dao.LectureRepository
import com.mkroo.lmsdemo.domain.Lecture
import com.mkroo.lmsdemo.domain.Student
import com.mkroo.lmsdemo.domain.Teacher
import com.mkroo.lmsdemo.dto.LectureApplyStatus
import com.mkroo.lmsdemo.helper.Fixture
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

@DataJpaTest
class LectureListingServiceTest(
    private val lectureRepository: LectureRepository,
    private val accountRepository: AccountRepository,
    private val entityManager: EntityManager,
) : BehaviorSpec({
    Given("강의 목록을 정렬할때") {
        val lectureListingService = LectureListingService(entityManager)

        val students = Fixture.getBuilder<Student>().sampleList(10).map { accountRepository.save(it) }
        val teacher = accountRepository.save(Fixture.sample<Teacher>()) as Teacher

        val applyFourOfTen = Lecture(
            title = "apply 4/10 0.4",
            teacher = teacher,
            maxStudentCount = 10,
            price = 100000,
        ).apply { students.take(4).forEach(::apply) }

        val applyFiveOfTwenty = Lecture(
            title = "apply 5/20 0.25",
            teacher = teacher,
            maxStudentCount = 20,
            price = 100000,
        ).apply { students.take(5).forEach(::apply) }

        val applySixOfTwelve = Lecture(
            title = "apply 6/12 0.5",
            teacher = teacher,
            maxStudentCount = 12,
            price = 100000,
        ).apply { students.take(6).forEach(::apply) }

        val lastCreatedLecture = Lecture(
            title = "lastCreatedLecture",
            teacher = teacher,
            maxStudentCount = 10,
            price = 100000,
        )

        lectureRepository.save(applyFourOfTen)
        lectureRepository.save(applyFiveOfTwenty)
        lectureRepository.save(applySixOfTwelve)
        lectureRepository.save(lastCreatedLecture)

        fun convertLectureApplyStatus(lecture: Lecture): LectureApplyStatus {
            return LectureApplyStatus(
                lecture.id,
                lecture.title,
                lecture.price,
                lecture.teacher.name,
                lecture.applicationCount,
                lecture.maxStudentCount,
                lecture.createdAt
            )
        }

        Then("페이징 결과를 반환한다") {
            val pageable = PageRequest.of(0, 1)

            val lectureApplyStatues = lectureListingService.listLectures(pageable)

            lectureApplyStatues.totalElements shouldBe 4
            lectureApplyStatues.content.size shouldBe 1
        }

        When("최근 등록순으로 정렬하면") {

            val sort = Sort.by(Sort.Order.desc("createdAt"))
            val pageable = PageRequest.of(0, 20, sort)

            Then("가장 마지막에 등록한 강의가 첫번째로 조회된다") {
                val lectureApplyStatues = lectureListingService.listLectures(pageable)

                lectureApplyStatues.content shouldBe listOf(
                    convertLectureApplyStatus(lastCreatedLecture),
                    convertLectureApplyStatus(applySixOfTwelve),
                    convertLectureApplyStatus(applyFiveOfTwenty),
                    convertLectureApplyStatus(applyFourOfTen),
                )
            }
        }

        When("신청자 많은 순으로 정렬하면") {
            val sort = Sort.by(Sort.Order.desc("applicationCount"))
            val pageable = PageRequest.of(0, 20, sort)

            Then("신청자가 가장 많은 강의가 첫번째로 조회된다") {
                val lectureApplyStatues = lectureListingService.listLectures(pageable)

                lectureApplyStatues.content shouldBe listOf(
                    convertLectureApplyStatus(applySixOfTwelve),
                    convertLectureApplyStatus(applyFiveOfTwenty),
                    convertLectureApplyStatus(applyFourOfTen),
                    convertLectureApplyStatus(lastCreatedLecture),
                )
            }
        }

        When("신청률 높은 순으로 정렬하면") {
            val sort = Sort.by(Sort.Order.desc("applicationRate"))
            val pageable = PageRequest.of(0, 20, sort)

            Then("신청률이 가장 높은 강의가 첫번째로 조회된다") {
                val lectureApplyStatues = lectureListingService.listLectures(pageable)

                lectureApplyStatues.content shouldBe listOf(
                    convertLectureApplyStatus(applySixOfTwelve),
                    convertLectureApplyStatus(applyFourOfTen),
                    convertLectureApplyStatus(applyFiveOfTwenty),
                    convertLectureApplyStatus(lastCreatedLecture),
                )
            }
        }
    }
})
