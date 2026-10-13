package com.example.app.domain.service

data class VoiceProfile(
    val id: String,
    val displayName: String,
    val description: String,
    val voiceName: String,
    val localeTag: String,
    val pitch: Float = 1.0f,
    val speechRate: Float = 1.0f,
    val pauseBetweenSentencesMs: Long = 250L,
    val pauseBetweenParagraphsMs: Long = 600L,
)

object VoicePresets {

    val VIETNAMESE: List<VoiceProfile> = listOf(
        VoiceProfile(
            id = "vi_natural",
            displayName = "Tu nhien",
            description = "Giong doc chuan, toc do binh thuong",
            voiceName = "",
            localeTag = "vi-VN",
            pitch = 1.0f,
            speechRate = 1.0f,
        ),
        VoiceProfile(
            id = "vi_slow",
            displayName = "Cham rai",
            description = "Phu hop tai lieu ky thuat",
            voiceName = "",
            localeTag = "vi-VN",
            pitch = 0.95f,
            speechRate = 0.8f,
            pauseBetweenSentencesMs = 400L,
            pauseBetweenParagraphsMs = 900L,
        ),
        VoiceProfile(
            id = "vi_fast",
            displayName = "Nhanh",
            description = "Tiet kiem thoi gian",
            voiceName = "",
            localeTag = "vi-VN",
            pitch = 1.0f,
            speechRate = 1.4f,
            pauseBetweenSentencesMs = 150L,
        ),
        VoiceProfile(
            id = "vi_dramatic",
            displayName = "Bieu cam",
            description = "Nhan nha, phu hop truyen",
            voiceName = "",
            localeTag = "vi-VN",
            pitch = 1.15f,
            speechRate = 1.05f,
            pauseBetweenSentencesMs = 500L,
        ),
        VoiceProfile(
            id = "vi_male_deep",
            displayName = "Nam tram",
            description = "Giong nam pitch thap",
            voiceName = "",
            localeTag = "vi-VN",
            pitch = 0.7f,
            speechRate = 0.95f,
        ),
        VoiceProfile(
            id = "vi_female_soft",
            displayName = "Nu nhe",
            description = "Giong nu pitch cao nhe",
            voiceName = "",
            localeTag = "vi-VN",
            pitch = 1.25f,
            speechRate = 1.0f,
        ),
    )

    val ENGLISH: List<VoiceProfile> = listOf(
        VoiceProfile(
            id = "en_us_natural",
            displayName = "Natural US",
            description = "American English, normal speed",
            voiceName = "",
            localeTag = "en-US",
            pitch = 1.0f,
            speechRate = 1.0f,
        ),
        VoiceProfile(
            id = "en_gb_natural",
            displayName = "British",
            description = "British English",
            voiceName = "",
            localeTag = "en-GB",
            pitch = 1.0f,
            speechRate = 1.0f,
        ),
    )

    fun forLocale(localeTag: String): List<VoiceProfile> = when {
        localeTag.startsWith("vi") -> VIETNAMESE
        localeTag.startsWith("en") -> ENGLISH
        else -> VIETNAMESE
    }

    fun findById(id: String): VoiceProfile =
        (VIETNAMESE + ENGLISH).firstOrNull { it.id == id } ?: VIETNAMESE.first()
}