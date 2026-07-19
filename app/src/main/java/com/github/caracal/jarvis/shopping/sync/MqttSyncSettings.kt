package com.github.caracal.jarvis.shopping.sync

/**
 * User-configurable connection settings for the Shopping List's HiveMQ Cloud sync.
 *
 * @param enabled Whether cloud sync should connect at all.
 * @param host The MQTT broker hostname.
 * @param port The MQTT broker TLS port.
 * @param username The MQTT broker username.
 * @param password The MQTT broker password.
 */
data class MqttSyncSettings(
    val enabled: Boolean,
    val host: String,
    val port: Int,
    val username: String,
    val password: String
)
