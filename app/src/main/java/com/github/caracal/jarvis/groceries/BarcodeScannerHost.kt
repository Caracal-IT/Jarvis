package com.github.caracal.jarvis.groceries

/**
 * Implemented by GroceriesActivity to allow fragments to open the barcode scanner
 * or OCR scanner in a dedicated overlay container, without disturbing the NavGraph.
 */
interface BarcodeScannerHost {
    fun openBarcodeScanner(onResult: (String) -> Unit)
    fun openOcrScanner(onResult: (String) -> Unit)
}
