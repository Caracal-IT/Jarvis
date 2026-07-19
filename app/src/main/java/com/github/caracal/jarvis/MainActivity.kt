package com.github.caracal.jarvis

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.caracal.jarvis.databinding.MainActivityBinding

/**
 * Main entry-point activity for the Jarvis application.
 *
 * Displays the four feature buttons on the home screen and routes the user
 * to the appropriate feature activity when a button is tapped.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnShopping.setOnClickListener {
            startActivity(Intent(this, ShoppingActivity::class.java))
        }

        binding.btnSystem.setOnClickListener {
            startActivity(Intent(this, SystemSettingsActivity::class.java))
        }

        binding.btnNetwork.setOnClickListener {
            openPlaceholderFeature(getString(R.string.title_comms))
        }

        binding.btnPower.setOnClickListener {
            openPlaceholderFeature(getString(R.string.title_power))
        }
    }

    private fun openPlaceholderFeature(title: String) {
        val intent = Intent(this, FeaturePlaceholderActivity::class.java)
        intent.putExtra(FeaturePlaceholderActivity.EXTRA_TITLE, title)
        startActivity(intent)
    }
}