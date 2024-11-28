package com.mkroo.lmsdemo.helper

import com.mkroo.lmsdemo.domain.Lecture
import com.mkroo.lmsdemo.domain.LectureApplication
import com.mkroo.lmsdemo.domain.Student
import com.mkroo.lmsdemo.domain.Teacher
import com.mkroo.lmsdemo.dto.RegisterUserRequest
import com.navercorp.fixturemonkey.ArbitraryBuilder
import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.setExp
import net.jqwik.api.Arbitraries

object Fixture {
    val fixtureMonkey: FixtureMonkey = FixtureMonkey
        .builder()
        .plugin(KotlinPlugin())
        .register(RegisterUserRequest::class.java) {
            it.giveMeBuilder<RegisterUserRequest>()
                .setExp(RegisterUserRequest::name, nameArbitrary())
                .setExp(RegisterUserRequest::email, emailArbitrary())
                .setExp(RegisterUserRequest::phoneNumber, phoneNumberArbitrary())
                .setExp(RegisterUserRequest::password, passwordArbitrary())
                .setExp(RegisterUserRequest::role, Arbitraries.of("student", "teacher"))
        }
        .register(Teacher::class.java, ::teacherArbitrary)
        .register(Student::class.java, ::studentArbitrary)
        .register(Lecture::class.java, ::lectureArbitrary)
        .register(LectureApplication::class.java) {
            it.giveMeBuilder<LectureApplication>()
                .setExp(LectureApplication::id, idArbitrary())
                .setExp(LectureApplication::lecture, lectureArbitrary(it))
                .setExp(LectureApplication::student, studentArbitrary(it))
        }
        .build()

    inline fun <reified T>getBuilder(): ArbitraryBuilder<T> {
        return fixtureMonkey.giveMeBuilder(T::class.java)
    }

    inline fun <reified T>sample(): T {
        return getBuilder<T>().sample()
    }

    fun getPhoneNumber() = phoneNumberArbitrary().sample()
    fun getEmail() = emailArbitrary().sample()
    fun getPassword() = passwordArbitrary().sample()

    private fun idArbitrary() = Arbitraries.longs().greaterOrEqual(1)

    private fun nameArbitrary() = Arbitraries.strings().alpha()
        .ofMinLength(2)
        .ofMaxLength(10)

    private fun phoneNumberArbitrary() = Arbitraries.strings().numeric().ofLength(8)
        .map { "010-${it.chunked(4).joinToString("-")}" }

    private fun emailArbitrary() = Arbitraries.strings().alpha()
        .ofMinLength(5)
        .ofMaxLength(10)
        .map { "$it@test.com" }

    private fun passwordArbitrary() = Arbitraries.strings().alpha().numeric()
        .ofMinLength(6)
        .ofMaxLength(10)

    private fun studentArbitrary(fixtureMonkey: FixtureMonkey) : ArbitraryBuilder<Student> {
        return fixtureMonkey.giveMeBuilder<Student>()
            .setExp(Student::id, idArbitrary())
            .setExp(Student::email, emailArbitrary())
            .setExp(Student::phoneNumber, phoneNumberArbitrary())
            .setExp(Student::name, nameArbitrary())
            .setExp(Student::encodedPassword, passwordArbitrary())
    }

    private fun teacherArbitrary(fixtureMonkey: FixtureMonkey) : ArbitraryBuilder<Teacher> {
        return fixtureMonkey.giveMeBuilder<Teacher>()
            .setExp(Teacher::id, idArbitrary())
            .setExp(Teacher::email, emailArbitrary())
            .setExp(Teacher::phoneNumber, phoneNumberArbitrary())
            .setExp(Teacher::name, nameArbitrary())
            .setExp(Teacher::encodedPassword, passwordArbitrary())
    }

    private fun lectureArbitrary(fixtureMonkey: FixtureMonkey) : ArbitraryBuilder<Lecture> {
        return fixtureMonkey.giveMeBuilder<Lecture>()
            .setExp(Lecture::id, idArbitrary())
            .setExp(Lecture::title, nameArbitrary())
            .setExp(Lecture::maxStudentCount, Arbitraries.integers().greaterOrEqual(1))
            .setExp(Lecture::price, Arbitraries.integers().greaterOrEqual(1).map { num -> num * 1000 })
            .setExp(Lecture::teacher, teacherArbitrary(fixtureMonkey))
    }
}
