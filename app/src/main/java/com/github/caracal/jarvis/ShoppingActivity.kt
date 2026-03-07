package com.github.caracal.jarvis

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.github.caracal.jarvis.databinding.ShoppingActivityBinding
import com.github.caracal.jarvis.shopping.ShoppingFragment

/**
 * Host activity for the Shopping feature.
 *
 * Currently displays the [ShoppingFragment] placeholder.
 * Replace [ShoppingFragment] with the real implementation when ready.
 */
class ShoppingActivity : AppCompatActivity() {

    private lateinit var binding: ShoppingActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ShoppingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.shoppingContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(binding.shoppingContainer.id, ShoppingFragment())
            }
        }
    }
}
