package com.github.caracal.jarvis.shopping.sync

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.github.caracal.jarvis.shopping.data.ShoppingSyncPublisher
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import java.nio.charset.StandardCharsets
import java.util.UUID

/**
 * Syncs the Shopping List across devices over a HiveMQ Cloud MQTT broker.
 *
 * Publishes locally-originated state changes as a retained message on [TOPIC], and applies
 * incoming retained/live messages via [onSnapshotReceived]. Connection is expected to be driven
 * by the app's foreground/background lifecycle via [connect] and [disconnect]. The subscription
 * to [TOPIC] is (re-)established automatically on every successful connect, including automatic
 * reconnects after a dropped connection, so remote changes keep being received for the lifetime
 * of this client.
 *
 * @param host The MQTT broker hostname.
 * @param port The MQTT broker TLS port.
 * @param username The MQTT broker username.
 * @param password The MQTT broker password.
 */
class ShoppingMqttSyncClient(
    private val host: String,
    private val port: Int,
    private val username: String,
    private val password: String
) : ShoppingSyncPublisher {

    /** Invoked on the main thread with the raw snapshot JSON whenever a message is received. */
    var onSnapshotReceived: ((String) -> Unit)? = null

    private val mainHandler = Handler(Looper.getMainLooper())
    private val client: Mqtt5AsyncClient = MqttClient.builder()
        .useMqttVersion5()
        .identifier("jarvis-android-${UUID.randomUUID()}")
        .serverHost(host)
        .serverPort(port)
        .sslWithDefaultConfig()
        .automaticReconnectWithDefaultConfig()
        // The client does not use a persistent session, so an automatic reconnect after a
        // dropped connection (network blip, backgrounding, broker-side timeout, etc.) re-opens
        // the MQTT session but does NOT restore the topic subscription on its own. Without this
        // listener, a device silently stops receiving other devices' published changes after the
        // first reconnect. Re-subscribing here on every ConnAck (initial connect and every
        // automatic reconnect alike) keeps the subscription alive for the client's lifetime.
        .addConnectedListener { subscribe() }
        .buildAsync()

    private var connectRequested = false

    /** Connects to the broker. Safe to call repeatedly. */
    fun connect() {
        if (connectRequested) return
        connectRequested = true
        client.connectWith()
            .simpleAuth()
            .username(username)
            .password(password.toByteArray(StandardCharsets.UTF_8))
            .applySimpleAuth()
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.e(TAG, "Failed to connect to MQTT broker.", throwable)
                    return@whenComplete
                }
                Log.i(TAG, "Connected to MQTT broker at $host:$port.")
            }
    }

    /** Disconnects from the broker. Safe to call repeatedly, including when not connected. */
    fun disconnect() {
        connectRequested = false
        client.disconnect().whenComplete { _, throwable ->
            if (throwable != null) {
                Log.w(TAG, "Error while disconnecting from MQTT broker.", throwable)
            }
        }
    }

    override fun publish(snapshotJson: String) {
        if (!client.state.isConnected) {
            Log.w(TAG, "Skipping publish; not connected to MQTT broker.")
            return
        }
        client.publishWith()
            .topic(TOPIC)
            .qos(MqttQos.AT_LEAST_ONCE)
            .retain(true)
            .payload(snapshotJson.toByteArray(StandardCharsets.UTF_8))
            .send()
            .whenComplete { result, throwable ->
                // For QoS 1/2, a broker-side rejection (e.g. an ACL denial) surfaces as an error
                // inside the result rather than as a thrown exception — the future still completes
                // normally, so it must be checked explicitly or a rejected publish looks identical
                // to a successful one.
                val nackError = result?.error?.orElse(null)
                when {
                    throwable != null -> Log.e(TAG, "Failed to publish shopping list snapshot.", throwable)
                    nackError != null -> Log.e(TAG, "Shopping list snapshot publish was rejected by the broker.", nackError)
                    else -> Log.i(TAG, "Published shopping list snapshot (${snapshotJson.length} bytes).")
                }
            }
    }

    private fun subscribe() {
        client.subscribeWith()
            .topicFilter(TOPIC)
            .qos(MqttQos.AT_LEAST_ONCE)
            .callback { publish ->
                val payload = String(
                    publish.payloadAsBytes,
                    StandardCharsets.UTF_8
                )
                Log.i(TAG, "Received message on $TOPIC (${payload.length} bytes, retained=${publish.isRetain}).")
                mainHandler.post { onSnapshotReceived?.invoke(payload) }
            }
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) {
                    Log.e(TAG, "Failed to subscribe to shopping list sync topic.", throwable)
                } else {
                    Log.i(TAG, "Subscribed to $TOPIC.")
                }
            }
    }

    companion object {
        private const val TAG = "ShoppingMqttSyncClient"
        private const val TOPIC = "jarvis/shopping-list/v1/state"
    }
}
