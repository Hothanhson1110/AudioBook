package com.example.app.core.common

import app.cash.turbine.test
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ResourceTest {

    @Test
    @DisplayName("asResource emits Loading then Success")
    fun asResourceEmitsLoadingThenSuccess() = runTest {
        flowOf(1, 2, 3).asResource().test {
            assertEquals(Resource.Loading, awaitItem())
            assertEquals(Resource.Success(1), awaitItem())
            assertEquals(Resource.Success(2), awaitItem())
            assertEquals(Resource.Success(3), awaitItem())
            awaitComplete()
        }
    }

    @Test
    @DisplayName("asResource catches exception")
    fun asResourceCatchesException() = runTest {
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
            awaitComplete()
        }
    }

    @Test
    @DisplayName("getOrDefault returns default for non-success")
    fun getOrDefaultReturnsDefault() {
        assertEquals(10, Resource.Loading.getOrDefault(10))
        assertEquals(10, Resource.Error(IllegalStateException()).getOrDefault(10))
        assertEquals(5, Resource.Success(5).getOrDefault(10))
    }
}