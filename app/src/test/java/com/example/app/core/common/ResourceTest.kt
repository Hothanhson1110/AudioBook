package com.example.app.core.common

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ResourceTest {

    @Test
    @DisplayName("asResource emit Loading → Success… → complete")
    fun asResource_emitsLoadingThenSuccess() = runTest {
        flowOf(1, 2, 3).asResource().test {
            assertEquals(Resource.Loading, awaitItem())
            assertEquals(Resource.Success(1), awaitItem())
            assertEquals(Resource.Success(2), awaitItem())
            assertEquals(Resource.Success(3), awaitItem())
            awaitComplete()
        }
    }

    @Test
    @DisplayName("asResource bắt exception → emit Error rồi complete")
    fun asResource_emitsErrorOnException() = runTest {
        val boom = IllegalStateException("boom")
        val upstream = flow<Int> {
            emit(1)
            throw boom
        }

        upstream.asResource().test {
            assertEquals(Resource.Loading, awaitItem())
            assertEquals(Resource.Success(1), awaitItem())
            val err = awaitItem()
            assertTrue(err is Resource.Error)
            assertEquals(boom, (err as Resource.Error).throwable)
            assertEquals("boom", err.message)
            awaitComplete()
        }
    }

    @Test
    @DisplayName("emitLoading=false bỏ qua Loading ban đầu")
    fun asResource_withoutLoading() = runTest {
        flowOf(42).asResource(emitLoading = false).test {
            assertEquals(Resource.Success(42), awaitItem())
            awaitComplete()
        }
    }

    @Test
    @DisplayName("map biến đổi Success, giữ nguyên Loading/Error")
    fun map_transformsOnlySuccess() {
        assertEquals(Resource.Loading, Resource.Loading.map { it })
        assertEquals(Resource.Success(2), Resource.Success(1).map { it * 2 })

        val err = Resource.Error(IllegalStateException("x"))
        assertEquals(err, err.map { it })
    }

    @Test
    @DisplayName("getOrDefault trả default khi không phải Success")
    fun getOrDefault_returnsDefaultForNonSuccess() {
        assertEquals(10, Resource.Loading.getOrDefault(10))
        assertEquals(10, Resource.Error(IllegalStateException()).getOrDefault(10))
        assertEquals(5, Resource.Success(5).getOrDefault(10))
    }
}