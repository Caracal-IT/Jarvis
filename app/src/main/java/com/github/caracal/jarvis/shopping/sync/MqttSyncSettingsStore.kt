package com.github.caracal.jarvis.shopping.sync

import android.content.Context
import androidx.core.content.edit
import com.github.caracal.jarvis.BuildConfig

/**
 * Persists user-editable [MqttSyncSettings] for the Shopping List's cloud sync.
 *
 * Falls back to the build-time defaults from `local.properties` (via [BuildConfig]) until the
 * user saves their own values from the System settings screen.
 *
 * @param context Application context used to access SharedPreferences.
 */
class MqttSyncSettingsStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Loads the current settings, falling back to [BuildConfig] defaults where unset. */
    fun load(): MqttSyncSettings = MqttSyncSettings(
        enabled = prefs.getBoolean(KEY_ENABLED, true),
        host = prefs.getString(KEY_HOST, null) ?: BuildConfig.HIVEMQ_HOST,
        port = prefs.getInt(KEY_PORT, -1).takeIf { it > 0 }
            ?: BuildConfig.HIVEMQ_PORT.toIntOrNull()
            ?: DEFAULT_PORT,
        username = prefs.getString(KEY_USERNAME, null) ?: BuildConfig.HIVEMQ_USERNAME,
        password = prefs.getString(KEY_PASSWORD, null) ?: BuildConfig.HIVEMQ_PASSWORD
    )

    /** Persists the given settings, overriding any [BuildConfig] defaults. */
    fun save(settings: MqttSyncSettings) {
        prefs.edit {
            putBoolean(KEY_ENABLED, settings.enabled)
            putString(KEY_HOST, settings.host)
            putInt(KEY_PORT, settings.port)
            putString(KEY_USERNAME, settings.username)
            putString(KEY_PASSWORD, settings.password)
        }
    }

    companion object {
        private const val PREFS_NAME = "mqtt_sync_settings"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_HOST = "host"
        private const val KEY_PORT = "port"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val DEFAULT_PORT = 8883
    }
}
