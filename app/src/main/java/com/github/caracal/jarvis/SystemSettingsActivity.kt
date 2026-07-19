package com.github.caracal.jarvis

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.caracal.jarvis.databinding.SystemSettingsActivityBinding
import com.github.caracal.jarvis.shopping.sync.MqttSyncSettings

/**
 * System settings screen. Currently hosts the Shopping List's HiveMQ Cloud sync configuration:
 * enabling/disabling sync and editing the broker host, port, username, and password.
 */
class SystemSettingsActivity : AppCompatActivity() {

    private lateinit var binding: SystemSettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = SystemSettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.systemSettingsRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener { finish() }

        val settingsStore = (application as JarvisApplication).syncSettingsStore
        val currentSettings = settingsStore.load()
        binding.swEnableSync.isChecked = currentSettings.enabled
        binding.etMqttHost.setText(currentSettings.host)
        binding.etMqttPort.setText(currentSettings.port.toString())
        binding.etMqttUsername.setText(currentSettings.username)
        binding.etMqttPassword.setText(currentSettings.password)

        binding.btnSaveSyncSettings.setOnClickListener {
            val host = binding.etMqttHost.text?.toString().orEmpty().trim()
            val port = binding.etMqttPort.text?.toString()?.trim()?.toIntOrNull()

            binding.tilMqttHost.error = if (host.isBlank()) getString(R.string.error_mqtt_host_required) else null
            binding.tilMqttPort.error =
                if (port == null || port !in 1..65535) getString(R.string.error_mqtt_port_invalid) else null
            if (host.isBlank() || port == null || port !in 1..65535) return@setOnClickListener

            settingsStore.save(
                MqttSyncSettings(
                    enabled = binding.swEnableSync.isChecked,
                    host = host,
                    port = port,
                    username = binding.etMqttUsername.text?.toString().orEmpty(),
                    password = binding.etMqttPassword.text?.toString().orEmpty()
                )
            )
            (application as JarvisApplication).applySyncSettings()
            Toast.makeText(this, R.string.msg_sync_settings_saved, Toast.LENGTH_SHORT).show()
        }
    }
}
