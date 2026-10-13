package com.example.app.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Home : Route
    @Serializable data class Player(val bookId: Long) : Route
    @Serializable data object Library : Route
}