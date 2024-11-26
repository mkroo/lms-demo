package com.mkroo.lmsdemo.helper

import com.mkroo.lmsdemo.domain.Account
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
        .register(Account::class.java) {
            it.giveMeBuilder<Account>()
                .setExp(Account::id, Arbitraries.longs().greaterOrEqual(1))
                .setExp(Account::email, emailArbitrary())
                .setExp(Account::phoneNumber, phoneNumberArbitrary())
                .setExp(Account::name, nameArbitrary())
                .setExp(Account::encodedPassword, passwordArbitrary())
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
}
