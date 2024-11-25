package com.mkroo.lmsdemo.helper

import com.mkroo.lmsdemo.domain.User
import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.setExp
import net.jqwik.api.Arbitraries

object Fixture {
    private val fixtureMonkey = FixtureMonkey
        .builder()
        .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
        .build()

    fun getUser(): User {
        val builder = fixtureMonkey.giveMeBuilder<User>()
            .setExp(User::id, Arbitraries.longs().greaterOrEqual(1))
            .setExp(User::name, Arbitraries.strings().alpha())
            .setExp(User::email, emailArbitrary)
            .setExp(User::phoneNumber, Arbitraries.strings().numeric())

        return builder.sample()
    }

    fun getEmail(): String {
        return emailArbitrary.sample()
    }

    fun getPassword(): String {
        return Arbitraries.strings().alpha().numeric().ofMinLength(6).ofMaxLength(10).sample()
    }

    private val emailArbitrary = Arbitraries.strings().alpha()
        .ofMinLength(5)
        .ofMaxLength(10)
        .map { "$it@test.com" }
}
