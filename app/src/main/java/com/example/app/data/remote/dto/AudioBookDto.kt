package com.example.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AudioBookDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("source_url") val sourceUrl: String,
    @SerializedName("audio_url") val audioUrl: String?,
)