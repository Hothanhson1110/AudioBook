package com.example.app.data.remote

import com.example.app.data.remote.dto.AudioBookDto
import retrofit2.http.GET

interface ApiService {
    @GET("audiobooks")
    suspend fun fetchBooks(): List<AudioBookDto>
}