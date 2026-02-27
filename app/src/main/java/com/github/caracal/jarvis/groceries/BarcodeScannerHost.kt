package com.github.caracal.jarvis.groceries

/**
 * Implemented by GroceriesActivity to allow fragments to open the barcode scanner
 * in a dedicated overlay container, without disturbing the NavGraph.
 */
interface BarcodeScannerHost {
    fun openBarcodeScanner(onResult: (String) -> Unit)
}
