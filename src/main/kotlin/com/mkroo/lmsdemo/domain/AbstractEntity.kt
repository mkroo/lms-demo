package com.mkroo.lmsdemo.domain

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.domain.Persistable

@MappedSuperclass
abstract class AbstractEntity : Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
        protected set

    override fun getId(): Long = id
    override fun isNew(): Boolean = id == 0L
}
