package com.github.caracal.jarvis.groceries

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.github.caracal.jarvis.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Full-screen OCR scanner fragment using CameraX + ML Kit Text Recognition.
 *
 * Flow:
 *  - Live camera preview shown to user.
 *  - User points at product label and taps CAPTURE LABEL.
 *  - ML Kit reads all text from the image.
 *  - Best product-name candidate is extracted and returned via Fragment Result API.
 *  - AddItemDialog opens pre-filled with the extracted text.
 */
class OcrScannerFragment : Fragment() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private val mainHandler = Handler(Looper.getMainLooper())

    private val textRecognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera() else dismissScanner()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_ocr_scanner, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Tap anywhere on the preview to capture
        view.findViewById<View>(R.id.ocrCameraPreview).setOnClickListener {
            captureAndRecognize()
        }
        // Keep the capture button as a fallback
        view.findViewById<View>(R.id.btnCapture).setOnClickListener {
            captureAndRecognize()
        }
        view.findViewById<View>(R.id.btnCancelOcr).setOnClickListener {
            dismissScanner()
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val previewView = requireView().findViewById<PreviewView>(R.id.ocrCameraPreview)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                viewLifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun captureAndRecognize() {
        val statusView = requireView().findViewById<TextView>(R.id.tvOcrStatus)
        val captureBtn = requireView().findViewById<View>(R.id.btnCapture)
        val previewView = requireView().findViewById<View>(R.id.ocrCameraPreview)
        val scanFrame   = requireView().findViewById<View>(R.id.scanFrame)

        statusView.text = getString(R.string.ocr_scanning)
        captureBtn.isEnabled = false
        previewView.isClickable = false

        imageCapture.takePicture(
            cameraExecutor,
            object : ImageCapture.OnImageCapturedCallback() {

                @OptIn(ExperimentalGetImage::class)
                override fun onCaptureSuccess(proxy: ImageProxy) {
                    val mediaImage = proxy.image
                    if (mediaImage == null) {
                        proxy.close()
                        resetButton()
                        return
                    }

                    // Crop to the scan frame rectangle before running OCR
                    val croppedBitmap = cropToScanFrame(proxy, previewView, scanFrame)
                    val image = if (croppedBitmap != null) {
                        InputImage.fromBitmap(croppedBitmap, 0)
                    } else {
                        InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)
                    }

                    textRecognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            val bestName = pickBestProductLine(visionText.text)
                            val result = Bundle().apply { putString(RESULT_OCR_TEXT, bestName) }
                            setFragmentResult(REQUEST_KEY, result)
                        }
                        .addOnFailureListener { resetButton() }
                        .addOnCompleteListener { proxy.close() }
                }

                override fun onError(exception: ImageCaptureException) {
                    resetButton()
                }
            }
        )
    }

    /**
     * Crops the captured image proxy to the portion that corresponds to the
     * scan frame rectangle shown on screen, so OCR only reads text inside the box.
     *
     * The camera image may be rotated relative to the screen; we account for
     * the rotation via the imageInfo.rotationDegrees, then compute the crop
     * rectangle in image-space coordinates.
     */
    @OptIn(ExperimentalGetImage::class)
    private fun cropToScanFrame(
        proxy: ImageProxy,
        previewView: View,
        scanFrame: View
    ): android.graphics.Bitmap? {
        return try {
            val mediaImage = proxy.image ?: return null
            val rotation = proxy.imageInfo.rotationDegrees

            // Convert YUV image to Bitmap
            val yBuffer = mediaImage.planes[0].buffer
            val uBuffer = mediaImage.planes[1].buffer
            val vBuffer = mediaImage.planes[2].buffer
            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()
            val nv21 = ByteArray(ySize + uSize + vSize)
            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)

            val yuvImage = android.graphics.YuvImage(
                nv21,
                android.graphics.ImageFormat.NV21,
                mediaImage.width,
                mediaImage.height,
                null
            )
            val out = java.io.ByteArrayOutputStream()
            yuvImage.compressToJpeg(
                android.graphics.Rect(0, 0, mediaImage.width, mediaImage.height),
                90, out
            )
            val rawBytes = out.toByteArray()
            var fullBitmap = android.graphics.BitmapFactory.decodeByteArray(rawBytes, 0, rawBytes.size)

            // Rotate bitmap to match screen orientation
            if (rotation != 0) {
                val matrix = android.graphics.Matrix().apply { postRotate(rotation.toFloat()) }
                fullBitmap = android.graphics.Bitmap.createBitmap(
                    fullBitmap, 0, 0, fullBitmap.width, fullBitmap.height, matrix, true
                )
            }

            // Map scan frame position (screen coordinates) → image coordinates
            val previewW = previewView.width.toFloat()
            val previewH = previewView.height.toFloat()
            val imgW = fullBitmap.width.toFloat()
            val imgH = fullBitmap.height.toFloat()

            // Scale factors from preview pixels → image pixels
            val scaleX = imgW / previewW
            val scaleY = imgH / previewH

            // Scan frame position relative to the preview view
            val frameLeft   = (scanFrame.left   - previewView.left).coerceAtLeast(0)
            val frameTop    = (scanFrame.top    - previewView.top ).coerceAtLeast(0)
            val frameRight  = (scanFrame.right  - previewView.left).coerceAtMost(previewView.width)
            val frameBottom = (scanFrame.bottom - previewView.top ).coerceAtMost(previewView.height)

            val cropLeft   = (frameLeft   * scaleX).toInt().coerceIn(0, fullBitmap.width  - 1)
            val cropTop    = (frameTop    * scaleY).toInt().coerceIn(0, fullBitmap.height - 1)
            val cropWidth  = ((frameRight - frameLeft)  * scaleX).toInt()
                .coerceIn(1, fullBitmap.width  - cropLeft)
            val cropHeight = ((frameBottom - frameTop) * scaleY).toInt()
                .coerceIn(1, fullBitmap.height - cropTop)

            android.graphics.Bitmap.createBitmap(fullBitmap, cropLeft, cropTop, cropWidth, cropHeight)
        } catch (_: Exception) { null }
    }

    /**
     * Extracts the best product-name candidate from raw OCR text.
     *
     * Strategy:
     * 1. Split into lines, score each line.
     * 2. Filter out clearly non-name lines (prices, weights, codes, dates, URLs).
     * 3. Prefer lines that look like product names:
     *    - Multiple words
     *    - Mix of letters (more letters than digits)
     *    - Not all-lowercase (labels usually have some capitalisation)
     *    - Length between 4 and 60 characters
     * 4. Join the top 1–2 lines if they complement each other (e.g. brand + variant).
     */
    private fun pickBestProductLine(rawText: String): String {
        // Noise patterns to reject
        val noisePatterns = listOf(
            Regex("""^[\d\s.,:/\-+%]+$"""),                         // purely numeric / symbols
            Regex("""(?i)^\s*(ml|g|kg|l|oz|lb|net\s*wt)\b"""),     // weight/volume units
            Regex("""(?i)^\s*(best before|use by|exp|bb|lot|batch|manufactured|packed|sell by)"""),
            Regex("""(?i)^\s*(barcode|ean|upc|gtin|www\.|http|@)"""),
            Regex("""(?i)^\s*(ingredients|nutrition|allergen|contains|servings|serving size)"""),
            Regex("""(?i)^\s*[re]?\d{6,}"""),                       // long number codes
            Regex("""(?i)^\s*(tel|fax|email|po box|p\.o\.|vat|reg\.?\s*no)"""),
        )

        val lines = rawText.lines()
            .map { it.trim() }
            .filter { line ->
                line.length in 3..70 &&
                line.any { it.isLetter() } &&
                noisePatterns.none { it.containsMatchIn(line) }
            }

        if (lines.isEmpty()) {
            // Last resort: return longest line from raw text
            return rawText.lines()
                .map { it.trim() }
                .filter { it.length > 2 }
                .maxByOrNull { it.length } ?: rawText.trim()
        }

        // Score each line
        data class ScoredLine(val text: String, val score: Int)

        val scored = lines.map { line ->
            val words = line.split(Regex("\\s+")).filter { it.isNotBlank() }
            val letterCount = line.count { it.isLetter() }
            val digitCount  = line.count { it.isDigit() }
            val hasUpper    = line.any { it.isUpperCase() }
            val wordCount   = words.size
            val lengthScore = line.length.coerceAtMost(40)

            var score = letterCount * 2 - digitCount * 3  // prefer letters over digits
            score += wordCount * 4                         // multi-word names score higher
            score += lengthScore                           // longer is often better (up to 40)
            if (hasUpper) score += 5                       // capitalised = likely a brand/product
            if (wordCount == 1) score -= 8                 // single word less likely to be a name
            if (line.all { it.isUpperCase() || !it.isLetter() }) score += 3 // all-caps labels

            ScoredLine(line, score)
        }.sortedByDescending { it.score }

        // Take top line; if second line complements it (adds variant/size info), append it
        val first = scored.getOrNull(0)?.text ?: return ""
        val second = scored.getOrNull(1)?.text

        return if (second != null &&
            second.length in 3..40 &&
            !first.contains(second, ignoreCase = true) &&
            second.split(Regex("\\s+")).size in 1..4
        ) {
            toTitleCase("$first $second")
        } else {
            toTitleCase(first)
        }
    }

    /** Converts ALL CAPS text to Title Case for cleaner display. */
    private fun toTitleCase(input: String): String =
        input.trim().split(Regex("\\s+")).joinToString(" ") { word ->
            if (word.all { it.isUpperCase() }) {
                word.lowercase().replaceFirstChar { it.uppercaseChar() }
            } else word
        }

    private fun resetButton() {
        mainHandler.post {
            if (isAdded) {
                requireView().findViewById<View>(R.id.btnCapture).isEnabled = true
                requireView().findViewById<View>(R.id.ocrCameraPreview).isClickable = true
                requireView().findViewById<TextView>(R.id.tvOcrStatus).text =
                    getString(R.string.ocr_hint)
            }
        }
    }

    private fun dismissScanner() {
        requireActivity().findViewById<View>(R.id.scanner_container)?.isVisible = false
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        textRecognizer.close()
    }

    companion object {
        const val REQUEST_KEY  = "ocr_scan"
        const val RESULT_OCR_TEXT = "ocr_text"
    }
}

