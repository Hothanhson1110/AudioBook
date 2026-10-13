package com.example.app.domain.service

import com.example.app.domain.model.VoiceAge
import com.example.app.domain.model.VoiceGender
import com.example.app.domain.model.VoiceSettings
import com.example.app.domain.model.VoiceStyle

/**
 * Chuyen doi VoiceSettings (gender + age + style) thanh VoiceProfile
 * voi pitch/speed/pause cu the cho TTS engine.
 */
object VoiceComposer {

    fun compose(settings: VoiceSettings): VoiceProfile {
        val basePitch = when (settings.gender) {
            VoiceGender.MALE -> 0.82f
            VoiceGender.FEMALE -> 1.18f
        }

        val agePitchDelta = when (settings.age) {
            VoiceAge.CHILD -> 0.28f
            VoiceAge.TEEN -> 0.12f
            VoiceAge.YOUNG -> 0.04f
            VoiceAge.ADULT -> 0f
            VoiceAge.MIDDLE -> -0.06f
            VoiceAge.ELDERLY -> -0.14f
        }

        val styleSpeed = when (settings.style) {
            VoiceStyle.NEUTRAL -> 1.00f
            VoiceStyle.SCIENCE -> 0.90f
            VoiceStyle.NOVEL -> 0.95f
            VoiceStyle.NEWS -> 1.15f
            VoiceStyle.COMEDY -> 1.10f
            VoiceStyle.ROMANCE -> 0.90f
            VoiceStyle.KNOWLEDGE -> 0.95f
            VoiceStyle.POETRY -> 0.85f
            VoiceStyle.DOCUMENTARY -> 0.95f
            VoiceStyle.STORY -> 1.00f
        }

        val stylePauseMs = when (settings.style) {
            VoiceStyle.SCIENCE -> 380L
            VoiceStyle.POETRY -> 650L
            VoiceStyle.NEWS -> 180L
            VoiceStyle.NOVEL -> 320L
            VoiceStyle.ROMANCE -> 400L
            VoiceStyle.DOCUMENTARY -> 320L
            else -> 250L
        }

        val pitch = (basePitch + agePitchDelta).coerceIn(0.5f, 2.0f)
        val speed = styleSpeed.coerceIn(0.5f, 2.0f)

        return VoiceProfile(
            id = "custom_${settings.gender.name}_${settings.age.name}_${settings.style.name}",
            displayName = settings.summary,
            description = settings.style.description,
            voiceName = "",
            localeTag = settings.localeTag,
            pitch = pitch,
            speechRate = speed,
            pauseBetweenSentencesMs = stylePauseMs,
            pauseBetweenParagraphsMs = stylePauseMs * 2,
        )
    }
}
