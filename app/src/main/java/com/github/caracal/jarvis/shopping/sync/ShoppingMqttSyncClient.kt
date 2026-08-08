package com.github.caracal.jarvis.shopping.sync

import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import com.github.caracal.jarvis.shopping.data.ShoppingSyncPublisher
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.zip.GZIPOutputStream

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

    /**
     * Publishes [json] as-is (no compression) as a one-off event to the receipt-data topic
     * (separate from and not retained like [TOPIC]). Invokes [onResult] on the main thread with
     * whether the publish succeeded.
     */
    fun publishReceiptData(json: String, onResult: (Boolean) -> Unit) {
        publishPayload(RECEIPT_DATA_TOPIC, json.toByteArray(StandardCharsets.UTF_8), "receipt data", onResult)
    }

    /**
     * Gzip-compresses and Base64-encodes [jpegBytes], wraps it in a `{encoding, data}` JSON
     * envelope (see [buildCompressedEnvelope]), then publishes it as a one-off event to the
     * receipt-photo topic (separate from and not retained like [TOPIC]). Invokes [onResult] on the
     * main thread with whether the publish succeeded.
     */
    fun publishReceiptPhoto(jpegBytes: ByteArray, onResult: (Boolean) -> Unit) {
        val envelope = buildCompressedEnvelope(jpegBytes).toByteArray(StandardCharsets.UTF_8)
        publishPayload(RECEIPT_PHOTO_TOPIC, envelope, "receipt photo", onResult)
    }

    private fun publishPayload(topic: String, payload: ByteArray, label: String, onResult: (Boolean) -> Unit) {
        if (!client.state.isConnected) {
            Log.w(TAG, "Skipping $label publish; not connected to MQTT broker.")
            mainHandler.post { onResult(false) }
            return
        }
        client.publishWith()
            .topic(topic)
            .qos(MqttQos.AT_LEAST_ONCE)
            .payload(payload)
            .send()
            .whenComplete { result, throwable ->
                val nackError = result?.error?.orElse(null)
                when {
                    throwable != null -> Log.e(TAG, "Failed to publish $label.", throwable)
                    nackError != null -> Log.e(TAG, "$label publish was rejected by the broker.", nackError)
                    else -> Log.i(TAG, "Published $label (${payload.size} bytes).")
                }
                mainHandler.post { onResult(throwable == null && nackError == null) }
            }
    }

    /** Gzip-compresses [bytes], Base64-encodes the result, and wraps it as `{"encoding", "data"}` JSON. */
    private fun buildCompressedEnvelope(bytes: ByteArray): String {
        val compressed = ByteArrayOutputStream().apply {
            GZIPOutputStream(this).use { it.write(bytes) }
        }.toByteArray()
        val base64Data = Base64.encodeToString(compressed, Base64.NO_WRAP)
        return JSONObject()
            .put("encoding", "gzip+base64")
            .put("data", base64Data)
            .toString()
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
        private const val RECEIPT_DATA_TOPIC = "jarvis/shopping-list/v1/receipt-data"
        private const val RECEIPT_PHOTO_TOPIC = "jarvis/shopping-list/v1/receipt-photo"
    }
}
