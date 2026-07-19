package com.github.caracal.jarvis

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.caracal.jarvis.databinding.FeaturePlaceholderActivityBinding

/**
 * Reusable placeholder activity for Home features that are not implemented yet.
 */
class FeaturePlaceholderActivity : AppCompatActivity() {

    private lateinit var binding: FeaturePlaceholderActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = FeaturePlaceholderActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.featurePlaceholderRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val title = intent.getStringExtra(EXTRA_TITLE) ?: getString(R.string.app_name)
        binding.tvFeatureTitle.text = title
    }

    companion object {
        const val EXTRA_TITLE: String = "extra_title"
    }
}

