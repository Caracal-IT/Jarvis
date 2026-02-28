package com.github.caracal.jarvis

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.github.caracal.jarvis.groceries.BarcodeScannerFragment
import com.github.caracal.jarvis.groceries.BarcodeScannerHost
import com.github.caracal.jarvis.groceries.OcrScannerFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class GroceriesActivity : AppCompatActivity(), BarcodeScannerHost {

    private var scannerCallback: ((String) -> Unit)? = null
    private var ocrCallback: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_groceries)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navView = findViewById<BottomNavigationView>(R.id.nav_view_groceries)
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_activity_groceries
        ) as NavHostFragment
        navView.setupWithNavController(navHostFragment.navController)

        // Close scanner on back press if it is visible
        onBackPressedDispatcher.addCallback(this,
            object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val container = findViewById<View>(R.id.scanner_container)
                    if (container.isVisible) {
                        hideScanner()
                        scannerCallback = null
                        ocrCallback = null
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )

        // Receive barcode result from the scanner fragment
        supportFragmentManager.setFragmentResultListener(
            BarcodeScannerFragment.REQUEST_KEY, this
        ) { _, bundle ->
            val barcode = bundle.getString(BarcodeScannerFragment.RESULT_BARCODE)
            hideScanner()
            barcode?.let { scannerCallback?.invoke(it) }
            scannerCallback = null
        }

        // Receive OCR text result from the OCR scanner fragment
        supportFragmentManager.setFragmentResultListener(
            OcrScannerFragment.REQUEST_KEY, this
        ) { _, bundle ->
            val text = bundle.getString(OcrScannerFragment.RESULT_OCR_TEXT)
            hideScanner()
            text?.let { ocrCallback?.invoke(it) }
            ocrCallback = null
        }
    }

    override fun openBarcodeScanner(onResult: (String) -> Unit) {
        scannerCallback = onResult
        findViewById<View>(R.id.scanner_container).isVisible = true
        supportFragmentManager.commit {
            replace(R.id.scanner_container, BarcodeScannerFragment())
            addToBackStack("scanner")
        }
    }

    override fun openOcrScanner(onResult: (String) -> Unit) {
        ocrCallback = onResult
        findViewById<View>(R.id.scanner_container).isVisible = true
        supportFragmentManager.commit {
            replace(R.id.scanner_container, OcrScannerFragment())
            addToBackStack("scanner")
        }
    }

    private fun hideScanner() {
        findViewById<View>(R.id.scanner_container).isVisible = false
        supportFragmentManager.popBackStack(
            "scanner", FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}
