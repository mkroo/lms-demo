package com.mkroo.lmsdemo.domain

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.proxy.HibernateProxy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.domain.Persistable
import java.time.LocalDateTime

@MappedSuperclass
abstract class AbstractEntity : Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L
        protected set

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now()

    @UpdateTimestamp
    val updatedAt: LocalDateTime = LocalDateTime.now()

    override fun getId(): Long = id
    override fun isNew(): Boolean = id == 0L

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (other !is HibernateProxy && this::class != other::class) {
            return false
        }

        return id == getIdentifier(other)
    }

    private fun getIdentifier(obj: Any): Long {
        return if (obj is HibernateProxy) {
            obj.hibernateLazyInitializer.identifier as Long
        } else {
            (obj as AbstractEntity).id
        }
    }

    override fun hashCode(): Int {
        return listOf(id, this.javaClass).hashCode()
    }
}
