package com.github.caracal.jarvis

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Main entry-point activity for the Jarvis application.
 *
 * Displays the four feature buttons on the home screen and routes the user
 * to the appropriate feature activity when a button is tapped.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_activity)

        val rootView = findViewById<android.view.View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnShopping).setOnClickListener {
            startActivity(Intent(this, ShoppingActivity::class.java))
        }

        findViewById<Button>(R.id.btnSystem).setOnClickListener {
            openPlaceholderFeature(getString(R.string.title_system))
        }

        findViewById<Button>(R.id.btnNetwork).setOnClickListener {
            openPlaceholderFeature(getString(R.string.title_comms))
        }

        findViewById<Button>(R.id.btnPower).setOnClickListener {
            openPlaceholderFeature(getString(R.string.title_power))
        }
    }

    private fun openPlaceholderFeature(title: String) {
        val intent = Intent(this, FeaturePlaceholderActivity::class.java)
        intent.putExtra(FeaturePlaceholderActivity.EXTRA_TITLE, title)
        startActivity(intent)
    }
}