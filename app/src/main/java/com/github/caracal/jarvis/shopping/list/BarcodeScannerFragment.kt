package com.github.caracal.jarvis.shopping.list

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.ShoppingListFragmentScanBinding
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat.getMainExecutor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Full-screen dialog fragment that opens the camera and scans for barcodes using ML Kit.
 *
 * On successful scan:
 * - If [MODE_FOR_EDIT], delivers a result via [setFragmentResult] with key [RESULT_KEY].
 * - If [MODE_FOR_LIST], checks if barcode exists in the Shopping List via ViewModel;
 *   if not found, replaces itself with [BarcodeResultFragment].
 */
class BarcodeScannerFragment : DialogFragment() {

    private var _binding: ShoppingListFragmentScanBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraExecutor: ExecutorService
    private var scanned = false

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera() else {
            Toast.makeText(requireContext(), getString(R.string.msg_camera_permission_denied), Toast.LENGTH_LONG).show()
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ShoppingListFragmentScanBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        binding.btnBack.setOnClickListener { dismiss() }
        cameraExecutor = Executors.newSingleThreadExecutor()
        return dialog
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(requireContext())
        future.addListener({
            val provider = future.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.cameraPreview.surfaceProvider
            }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analyzer ->
                    analyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                        analyzeImage(imageProxy)
                    }
                }

            provider.unbindAll()
            provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
        }, getMainExecutor(requireContext()))
    }

    private fun analyzeImage(proxy: ImageProxy) {
        if (scanned) {
            proxy.close()
            return
        }

        val nv21 = imageProxyToNv21(proxy)
        val image = InputImage.fromByteArray(
            nv21,
            proxy.width,
            proxy.height,
            proxy.imageInfo.rotationDegrees,
            InputImage.IMAGE_FORMAT_NV21
        )

        BarcodeScanning.getClient().process(image)
            .addOnSuccessListener { barcodes -> handleBarcodes(barcodes) }
            .addOnCompleteListener { proxy.close() }
    }

    /** Converts a YUV_420_888 [ImageProxy] to NV21 byte array for ML Kit input. */
    private fun imageProxyToNv21(image: ImageProxy): ByteArray {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val width = image.width
        val height = image.height
        val nv21 = ByteArray(ySize + (width * height / 2))

        yBuffer.get(nv21, 0, ySize)

        val uRowStride = image.planes[1].rowStride
        val uPixelStride = image.planes[1].pixelStride
        val vRowStride = image.planes[2].rowStride
        val vPixelStride = image.planes[2].pixelStride

        var offset = ySize
        for (row in 0 until height / 2) {
            for (col in 0 until width / 2) {
                val vIndex = row * vRowStride + col * vPixelStride
                val uIndex = row * uRowStride + col * uPixelStride
                nv21[offset++] = vBuffer.get(vIndex)
                nv21[offset++] = uBuffer.get(uIndex)
            }
        }

        return nv21
    }

    private fun handleBarcodes(barcodes: List<Barcode>) {
        if (scanned || barcodes.isEmpty()) return
        val raw = barcodes.firstOrNull { it.rawValue != null }?.rawValue ?: return
        scanned = true

        val mode = arguments?.getString(ARG_MODE) ?: MODE_FOR_EDIT

        requireActivity().runOnUiThread {
            if (mode == MODE_FOR_EDIT) {
                // Return barcode to the edit screen
                setFragmentResult(RESULT_KEY, Bundle().apply {
                    putString(RESULT_BARCODE, raw)
                })
                dismiss()
            } else {
                // Check if the barcode already belongs to an item
                val shoppingViewModel = try {
                    (requireParentFragment().requireParentFragment()
                            as? com.github.caracal.jarvis.shopping.ShoppingFragment)?.viewModel
                } catch (_: Exception) { null }

                val existingItem = shoppingViewModel?.findByBarcode(raw)
                if (existingItem != null) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.msg_barcode_found, existingItem.name),
                        Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                } else {
                    // Replace it with the result fragment so the user can link or add new
                    dismiss()
                    BarcodeResultFragment.newInstance(raw)
                        .show(requireParentFragment().childFragmentManager, TAG_RESULT)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }

    companion object {
        const val RESULT_KEY = "barcode_scanner_result"
        const val RESULT_BARCODE = "barcode"
        private const val ARG_MODE = "mode"
        private const val MODE_FOR_EDIT = "edit"
        private const val MODE_FOR_LIST = "list"
        private const val TAG_RESULT = "barcode_result"

        /** Use when scanning a barcode to add to an item being edited. */
        fun newInstanceForEdit() = BarcodeScannerFragment().apply {
            arguments = Bundle().apply { putString(ARG_MODE, MODE_FOR_EDIT) }
        }

        /** Use when scanning a barcode from the shopping list FAB. */
        fun newInstanceForList() = BarcodeScannerFragment().apply {
            arguments = Bundle().apply { putString(ARG_MODE, MODE_FOR_LIST) }
        }
    }
}
