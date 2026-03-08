package com.github.caracal.jarvis

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Reusable placeholder activity for Home features that are not implemented yet.
 */
class FeaturePlaceholderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.feature_placeholder_activity)

        val rootView = findViewById<android.view.View>(R.id.feature_placeholder_root)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val title = intent.getStringExtra(EXTRA_TITLE) ?: getString(R.string.app_name)
        findViewById<TextView>(R.id.tvFeatureTitle).text = title
    }

    companion object {
        const val EXTRA_TITLE: String = "extra_title"
    }
}

