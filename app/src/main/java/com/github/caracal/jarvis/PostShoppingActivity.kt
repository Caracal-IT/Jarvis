package com.github.caracal.jarvis

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.caracal.jarvis.databinding.PostShoppingActivityBinding
import com.github.caracal.jarvis.postshopping.ReceiptAiParser
import com.github.caracal.jarvis.postshopping.ReceiptData
import com.github.caracal.jarvis.postshopping.ReceiptItemAdapter
import com.github.caracal.jarvis.postshopping.ReceiptParser
import com.github.caracal.jarvis.postshopping.ReceiptRowReconstructor
import com.github.caracal.jarvis.postshopping.PositionedLine
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Host activity for the Post-Shopping feature.
 *
 * Opens the camera so the user can capture a still image of a store
 * receipt/slip on demand (tap-to-scan, not continuous live analysis). The
 * captured frame is read entirely in memory with on-device ML Kit text
 * recognition and, if a local AI model is bundled, [ReceiptAiParser] - it is
 * never written to disk. This is where all post-shopping tasks (e.g.
 * reconciling the receipt against the shopping list) will live going
 * forward.
 */
class PostShoppingActivity : AppCompatActivity() {

    private lateinit var binding: PostShoppingActivityBinding

    private var cameraExecutor: ExecutorService? = null

    /** Set by a capture tap; consumed by the next analyzed frame. */
    private val captureRequested = AtomicBoolean(false)

    /** True from the moment a capture is requested until a result (or failure) is shown. */
    private val isScanning = AtomicBoolean(false)
    private val receiptItemAdapter = ReceiptItemAdapter()
    private lateinit var receiptAiParser: ReceiptAiParser
    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(this, R.string.msg_camera_permission_denied, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = PostShoppingActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.postShoppingRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnCapture.setOnClickListener { onCaptureTapped() }

        binding.rvReceiptItems.layoutManager = LinearLayoutManager(this)
        binding.rvReceiptItems.adapter = receiptItemAdapter
        binding.rvReceiptItems.isNestedScrollingEnabled = false

        receiptAiParser = ReceiptAiParser(this)
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.cameraPreview.surfaceProvider
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    cameraExecutor?.let { executor ->
                        analysis.setAnalyzer(executor, ReceiptTextAnalyzer { visionText ->
                            onTextRecognized(visionText)
                        })
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Camera binding failed.", e)
                Toast.makeText(this, R.string.msg_camera_binding_failed, Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    /** Triggered by the shutter button: captures the next frame and processes it. */
    private fun onCaptureTapped() {
        if (!isScanning.compareAndSet(false, true)) return
        showProcessing(getString(R.string.status_capturing_image))

        if (BuildConfig.DEBUG && isEmulatorEnvironment()) {
            // Emulator virtual cameras can't be pointed at a real receipt, so
            // debug builds substitute a bundled sample slip image instead.
            processDebugSampleReceipt()
        } else {
            captureRequested.set(true)
        }
    }

    /** Runs the same OCR pipeline as a live capture, against a bundled sample slip image. */
    private fun processDebugSampleReceipt() {
        cameraExecutor?.execute {
            try {
                val bitmap = assets.open(DEBUG_SAMPLE_RECEIPT_ASSET).use { BitmapFactory.decodeStream(it) }
                val image = InputImage.fromBitmap(bitmap, 0)
                recognizer.process(image)
                    .addOnSuccessListener { visionText -> onTextRecognized(visionText) }
                    .addOnFailureListener { abortScan(R.string.msg_capture_failed) }
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Failed to load debug sample receipt.", e)
                abortScan(R.string.msg_capture_failed)
            }
        }
    }

    private fun isEmulatorEnvironment(): Boolean =
        Build.FINGERPRINT.contains("generic") ||
            Build.PRODUCT.contains("sdk") ||
            Build.HARDWARE.contains("goldfish") ||
            Build.HARDWARE.contains("ranchu") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for") ||
            Build.MANUFACTURER.contains("Genymotion")

    /**
     * ML Kit's [Text.text] concatenates text blocks in detection order, which
     * for a tilted or multi-column photo does not reliably match top-to-bottom
     * reading order, and a receipt's wide gap between the description and
     * price columns can make ML Kit report them as separate lines despite
     * sharing one printed row. [ReceiptRowReconstructor] rebuilds each
     * physical row from bounding-box geometry so [ReceiptParser] sees the
     * receipt the way a single-column printout would have produced it.
     */
    private fun readingOrderText(visionText: Text): String =
        ReceiptRowReconstructor.reconstruct(
            visionText.textBlocks.flatMap { it.lines }.mapNotNull { line ->
                val box = line.boundingBox ?: return@mapNotNull null
                PositionedLine(top = box.top, bottom = box.bottom, left = box.left, text = line.text)
            }
        )

    private fun onTextRecognized(visionText: Text) {
        val recognizedText = readingOrderText(visionText)
        if (recognizedText.isBlank()) {
            finishScan(receipt = null)
            return
        }
        android.util.Log.d(TAG, "Recognized text (reading order):\n$recognizedText")

        updateProcessingStatus(getString(R.string.status_reading_text))
        val heuristicReceipt = ReceiptParser.parse(recognizedText)
        android.util.Log.d(TAG, "Heuristic parse items: ${heuristicReceipt.items}")

        if (receiptAiParser.isAvailable()) {
            updateProcessingStatus(getString(R.string.status_ai_processing), showAiBadge = true)
            cameraExecutor?.execute {
                val aiReceipt = receiptAiParser.parse(recognizedText)
                val usedAi = aiReceipt != null && aiReceipt.items.isNotEmpty()
                runOnUiThread { finishScan(if (usedAi) aiReceipt else heuristicReceipt, usedAi) }
            }
        } else {
            finishScan(heuristicReceipt, usedAi = false)
        }
    }

    private fun finishScan(receipt: ReceiptData?, usedAi: Boolean = false) {
        runOnUiThread {
            hideProcessing()
            isScanning.set(false)
            if (receipt == null) {
                Toast.makeText(this, R.string.msg_no_text_detected, Toast.LENGTH_SHORT).show()
            } else {
                renderReceipt(receipt, usedAi)
            }
        }
    }

    /** Aborts the in-progress scan with a specific failure message. */
    private fun abortScan(@androidx.annotation.StringRes messageRes: Int) {
        runOnUiThread {
            hideProcessing()
            isScanning.set(false)
            Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderReceipt(receipt: ReceiptData, usedAi: Boolean) {
        binding.tvShopName.text = receipt.shopName ?: getString(R.string.label_default_shop_name)
        binding.tvDate.text = receipt.date.format(dateFormatter)

        binding.tvSourceBadge.visibility = View.VISIBLE
        binding.tvSourceBadge.text = getString(
            if (usedAi) R.string.label_source_ai else R.string.label_source_basic
        )

        receiptItemAdapter.submitList(receipt.items)
        binding.tvNoItemsDetected.visibility = if (receipt.items.isEmpty()) View.VISIBLE else View.GONE

        bindOptionalAmountRow(binding.rowSubtotalZeroRated, binding.tvSubtotalZeroRated, receipt.subtotalZeroRated)
        bindOptionalAmountRow(
            binding.rowSubtotalStandardRated, binding.tvSubtotalStandardRated, receipt.subtotalStandardRated
        )
        binding.tvSubtotal.text = formatAmount(receipt.subtotal)
        binding.tvTax.text = formatAmount(receipt.tax)
        binding.tvTotal.text = formatAmount(receipt.total)
    }

    /** Shows [row] with [amount] formatted into [valueView], or hides the row entirely when [amount] is null. */
    private fun bindOptionalAmountRow(row: View, valueView: android.widget.TextView, amount: Double?) {
        if (amount == null) {
            row.visibility = View.GONE
        } else {
            row.visibility = View.VISIBLE
            valueView.text = formatAmount(amount)
        }
    }

    private fun formatAmount(amount: Double?): String =
        if (amount == null) "-" else String.format(Locale.getDefault(), "%.2f", amount)

    private fun showProcessing(status: String) {
        runOnUiThread {
            binding.tvProcessingStatus.text = status
            binding.tvAiBadgeProcessing.visibility = View.GONE
            binding.processingOverlay.visibility = View.VISIBLE
        }
    }

    private fun updateProcessingStatus(status: String, showAiBadge: Boolean = false) {
        runOnUiThread {
            binding.tvProcessingStatus.text = status
            binding.tvAiBadgeProcessing.visibility = if (showAiBadge) View.VISIBLE else View.GONE
        }
    }

    private fun hideProcessing() {
        binding.processingOverlay.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor?.shutdown()
        receiptAiParser.close()
    }

    /** Analyzes camera frames but only runs OCR on the one frame following a capture tap. */
    private inner class ReceiptTextAnalyzer(
        private val onResult: (Text) -> Unit
    ) : ImageAnalysis.Analyzer {

        @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage == null || !captureRequested.compareAndSet(true, false)) {
                imageProxy.close()
                return
            }

            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(image)
                .addOnSuccessListener { visionText -> onResult(visionText) }
                .addOnFailureListener { abortScan(R.string.msg_capture_failed) }
                .addOnCompleteListener { imageProxy.close() }
        }
    }

    companion object {
        private const val TAG = "PostShoppingActivity"
        private const val DEBUG_SAMPLE_RECEIPT_ASSET = "sample_receipt.jpg"
    }
}
