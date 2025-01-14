package com.mkroo.lmsdemo.application

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.expression.Expression
import com.linecorp.kotlinjdsl.querymodel.jpql.expression.Expressions.customExpression
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.querymodel.jpql.sort.Sortable
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import com.mkroo.lmsdemo.domain.Lecture
import com.mkroo.lmsdemo.domain.LectureApplication
import com.mkroo.lmsdemo.domain.Teacher
import com.mkroo.lmsdemo.dto.LectureApplyStatus
import com.mkroo.lmsdemo.exception.LectureListingParamsException
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Order
import org.springframework.stereotype.Service

@Service
class LectureListingService(
    private val entityManager: EntityManager,
) {
    companion object {
        private val renderContext = JpqlRenderContext()
    }

    fun listLectures(pageable: Pageable): Page<LectureApplyStatus> {
        val selectQuery = jpql {
            selectNew<LectureApplyStatus>(
                path(Lecture::id),
                path(Lecture::title),
                path(Lecture::price),
                path(Lecture::teacher).path(Teacher::name),
                castToInt(count(entity(LectureApplication::class))),
                path(Lecture::maxStudentCount),
                path(Lecture::createdAt)
            ).from(
                entity(Lecture::class),
                leftJoin(LectureApplication::class).on(
                    path(Lecture::id).equal(
                        path(LectureApplication::lecture).path(Lecture::id)
                    )
                ),
            ).groupBy(
                entity(Lecture::class)
            ).orderBy(
                *pageable.sort.map { convertOrder(it) }.toList().toTypedArray()
            )
        }

        val countQuery = jpql {
            select(
                count(entity(Lecture::class))
            ).from(
                entity(Lecture::class)
            )
        }

        return PageImpl(getResultList(selectQuery, pageable), pageable, getMaxResults(countQuery))
    }

    private fun Jpql.convertOrder(order: Order) : Sortable {
        val sortExpression = when (order.property) {
            "createdAt" -> entity(Lecture::class).path(Lecture::createdAt)
            "applicationCount" -> count(entity(LectureApplication::class))
            "applicationRate" -> divideIntegers(count(entity(LectureApplication::class)), path(Lecture::maxStudentCount))
            else -> throw LectureListingParamsException("허용하지 않는 정렬 순서입니다. (${order.property})")
        }

        return if (order.isDescending) {
            sortExpression.desc()
        } else {
            sortExpression.asc()
        }
    }

    private fun divideIntegers(dividend: Expression<*>, divisor: Expression<*>): Expression<Double> {
        return customExpression(Double::class, "CAST({0} AS DOUBLE) / {1}", listOf(dividend, divisor))
    }

    private fun castToInt(expression: Expression<*>): Expression<Int> {
        return customExpression(Int::class, "CAST({0} AS INTEGER)", listOf(expression))
    }

    private fun getResultList(query: SelectQuery<LectureApplyStatus>, pageable: Pageable): List<LectureApplyStatus> {
        return entityManager.createQuery(query, renderContext)
            .setFirstResult(pageable.offset.toInt())
            .setMaxResults(pageable.pageSize)
            .resultList
    }

    private fun getMaxResults(countQuery: SelectQuery<Long>): Long {
        return entityManager.createQuery(countQuery, renderContext).singleResult
    }
}

