package com.github.caracal.jarvis.shopping.list

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getMainExecutor
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.ShoppingListFragmentScanBinding
import com.github.caracal.jarvis.shopping.ShoppingFragment
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Full-screen dialog fragment that opens the camera and scans for barcodes using ML Kit.
 *
 * On successful scan:
 * - If [MODE_FOR_EDIT], delivers a result via [setFragmentResult] with key [RESULT_KEY].
 * - If [MODE_FOR_LIST], checks if the barcode matches an item in the system.
 *   - If found and on shopping list: confirms it's already there.
 *   - If found and NOT on shopping list (in replenish list): adds it to the shopping list.
 *   - If not found: opens [BarcodeResultFragment] for manual linking or creation.
 */
class BarcodeScannerFragment : DialogFragment() {

    private var _binding: ShoppingListFragmentScanBinding? = null
    private val binding get() = _binding!!

    private var cameraExecutor: ExecutorService? = null
    private var mode: Int = MODE_FOR_LIST

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), R.string.msg_camera_permission_denied, Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mode = arguments?.getInt(ARG_MODE) ?: MODE_FOR_LIST
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ShoppingListFragmentScanBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.btnBack.setOnClickListener { dismiss() }

        return dialog
    }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = binding.cameraPreview.surfaceProvider
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor!!, BarcodeAnalyzer { barcodes ->
                        onBarcodesDetected(barcodes)
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (_: Exception) {
                Toast.makeText(requireContext(), "Camera binding failed.", Toast.LENGTH_SHORT).show()
            }
        }, getMainExecutor(requireContext()))
    }

    private fun onBarcodesDetected(barcodes: List<Barcode>) {
        if (barcodes.isEmpty()) return

        // We only process the first barcode found in the frame.
        val barcode = barcodes.first()
        val raw = barcode.rawValue ?: return

        // Process only on the main thread.
        activity?.runOnUiThread {
            if (mode == MODE_FOR_EDIT) {
                // Return the barcode to the edit screen.
                setFragmentResult(RESULT_KEY, Bundle().apply {
                    putString(RESULT_BARCODE, raw)
                })
                dismiss()
            } else {
                val shoppingViewModel =
                    (parentFragment?.parentFragment as? ShoppingFragment)?.viewModel
                val itemInSystem = shoppingViewModel?.findByBarcode(raw)

                if (itemInSystem != null) {
                    if (itemInSystem.isOnShoppingList) {
                        // Already in shopping list. Just confirm.
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.msg_barcode_already_linked, itemInSystem.name),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // In replenish list. Move to shopping list.
                        shoppingViewModel.addBaselineItemToShoppingList(itemInSystem.id)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.msg_item_added_to_list, itemInSystem.name),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    dismiss()
                } else {
                    // Not found in system at all. Open manual dialog.
                    dismiss()
                    BarcodeResultFragment.newInstance(raw, null)
                        .show(requireParentFragment().childFragmentManager, TAG_RESULT)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor?.shutdown()
        _binding = null
    }

    private class BarcodeAnalyzer(private val onResult: (List<Barcode>) -> Unit) : ImageAnalysis.Analyzer {
        private val scanner = BarcodeScanning.getClient()

        @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        if (barcodes.isNotEmpty()) {
                            onResult(barcodes)
                        }
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
    }

    companion object {
        const val RESULT_KEY = "barcode_scanner_result"
        const val RESULT_BARCODE = "scanned_barcode"

        private const val ARG_MODE = "mode"
        private const val MODE_FOR_EDIT = 1
        private const val MODE_FOR_LIST = 2

        private const val TAG_RESULT = "barcode_result"

        fun newInstanceForEdit() = BarcodeScannerFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_MODE, MODE_FOR_EDIT)
            }
        }

        fun newInstanceForList() = BarcodeScannerFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_MODE, MODE_FOR_LIST)
            }
        }
    }
}
