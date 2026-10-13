package com.example.app.data.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.app.domain.model.VoiceAge
import com.example.app.domain.model.VoiceGender
import com.example.app.domain.model.VoiceSettings
import com.example.app.domain.model.VoiceStyle
import com.example.app.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "audiobook_settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SettingsRepository {

    override fun observeVoiceSettings(): Flow<VoiceSettings> =
        context.dataStore.data.map { prefs ->
            VoiceSettings(
                gender = prefs[KEY_GENDER]
                    ?.let { runCatching { VoiceGender.valueOf(it) }.getOrNull() }
                    ?: VoiceGender.FEMALE,
                age = prefs[KEY_AGE]
                    ?.let { runCatching { VoiceAge.valueOf(it) }.getOrNull() }
                    ?: VoiceAge.YOUNG,
                style = prefs[KEY_STYLE]
                    ?.let { runCatching { VoiceStyle.valueOf(it) }.getOrNull() }
                    ?: VoiceStyle.NEUTRAL,
                localeTag = prefs[KEY_LOCALE] ?: "vi-VN",
            )
        }

    override suspend fun saveVoiceSettings(settings: VoiceSettings) {
        context.dataStore.edit { prefs ->
            prefs[KEY_GENDER] = settings.gender.name
            prefs[KEY_AGE] = settings.age.name
            prefs[KEY_STYLE] = settings.style.name
            prefs[KEY_LOCALE] = settings.localeTag
        }
    }

    companion object {
        private val KEY_GENDER = stringPreferencesKey("voice_gender")
        private val KEY_AGE = stringPreferencesKey("voice_age")
        private val KEY_STYLE = stringPreferencesKey("voice_style")
        private val KEY_LOCALE = stringPreferencesKey("voice_locale")
    }
}
