package com.github.caracal.jarvis

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.github.caracal.jarvis.shopping.data.ShoppingSyncPublisher
import com.github.caracal.jarvis.shopping.data.SharedPrefsShoppingRepository
import com.github.caracal.jarvis.shopping.data.ShoppingRepository
import com.github.caracal.jarvis.shopping.sync.MqttSyncSettingsStore
import com.github.caracal.jarvis.shopping.sync.ShoppingMqttSyncClient

/**
 * Application-wide composition root.
 *
 * Hosts the single, application-scoped [ShoppingRepository] instance so that ViewModels are
 * constructed with an injected dependency rather than each Fragment/Factory instantiating the
 * data source itself. This project's current Android Gradle Plugin version does not yet support
 * the Hilt Gradle plugin (see gradle/libs.versions.toml notes); this manual composition root
 * follows the same dependency-injection intent — a single source of truth for wiring
 * dependencies, no direct data-source construction in Fragments/Activities — until Hilt can be
 * adopted.
 *
 * Also wires up cloud sync for the Shopping List: the active [ShoppingMqttSyncClient] publishes
 * locally-made changes to HiveMQ Cloud and applies incoming changes from other devices, connecting
 * only while the app is in the foreground and sync is enabled in [syncSettingsStore]. [shoppingRepository]
 * is wired to a stable forwarding [ShoppingSyncPublisher] rather than a specific client instance so
 * that [applySyncSettings] can swap the underlying client at runtime (e.g. after the user edits
 * settings in [SystemSettingsActivity]) without reconstructing the repository.
 */
class JarvisApplication : Application() {

    val syncSettingsStore: MqttSyncSettingsStore by lazy { MqttSyncSettingsStore(this) }

    private var shoppingSyncClient: ShoppingMqttSyncClient? = null
    private var isForeground = false

    /** Single, application-scoped repository for all shopping data access. */
    val shoppingRepository: ShoppingRepository by lazy {
        SharedPrefsShoppingRepository(
            this,
            syncPublisher = { json -> shoppingSyncClient?.publish(json) }
        )
    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                isForeground = true
                shoppingSyncClient?.connect()
            }

            override fun onStop(owner: LifecycleOwner) {
                isForeground = false
                shoppingSyncClient?.disconnect()
            }
        })
        applySyncSettings()
    }

    /**
     * Rebuilds the MQTT sync client from the current [syncSettingsStore] settings, disconnecting
     * and discarding any previous client. Connects immediately if sync is enabled and the app is
     * currently in the foreground. Call after saving new settings from [SystemSettingsActivity].
     */
    fun applySyncSettings() {
        shoppingSyncClient?.disconnect()
        shoppingSyncClient = null

        val settings = syncSettingsStore.load()
        if (!settings.enabled || settings.host.isBlank()) return

        val client = ShoppingMqttSyncClient(
            host = settings.host,
            port = settings.port,
            username = settings.username,
            password = settings.password
        )
        client.onSnapshotReceived = { json -> shoppingRepository.applyRemoteSnapshot(json) }
        shoppingSyncClient = client
        if (isForeground) client.connect()
    }
}
