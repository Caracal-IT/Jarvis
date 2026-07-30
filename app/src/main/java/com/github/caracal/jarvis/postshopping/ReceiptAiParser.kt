package com.github.caracal.jarvis.postshopping

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions
import org.json.JSONObject
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeParseException

/**
 * Runs the on-device [LlmInference] (MediaPipe LLM Inference API, e.g. a
 * bundled Gemma `.task` model) to turn the raw OCR text off a scanned
 * receipt into structured [ReceiptData], entirely on-device with no network
 * calls.
 *
 * The model file is never shipped with the app (it can be hundreds of MBs to
 * a few GBs). Place a MediaPipe-compatible `.task` model
 * (e.g. Gemma-3 1B IT, downloaded from https://huggingface.co/litert-community)
 * at [modelFile] - for example by pushing it with:
 * `adb push model.task /sdcard/Android/data/com.github.caracal.jarvis/files/llm/model.task`
 *
 * If the model file is missing or fails to load, [isAvailable] returns
 * `false` and callers should fall back to [ReceiptParser]'s heuristic parsing.
 */
class ReceiptAiParser(context: Context) {

    private val applicationContext = context.applicationContext
    private val modelFile: File = File(context.getExternalFilesDir(MODEL_DIR), MODEL_FILE_NAME)

    private val llmInference: LlmInference? by lazy { createLlmInference() }

    /** Whether a local model file is present and was loaded successfully. */
    fun isAvailable(): Boolean = llmInference != null

    /**
     * Runs the local model against [rawText] and returns the structured
     * result, or `null` if the model is unavailable or its response could
     * not be parsed as valid receipt JSON.
     *
     * This performs blocking on-device inference and must be called off the
     * main thread.
     */
    fun parse(rawText: String): ReceiptData? {
        val inference = llmInference ?: return null

        return try {
            val response = inference.generateResponse(buildPrompt(rawText))
            parseModelResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "Local AI receipt parsing failed.", e)
            null
        }
    }

    private fun createLlmInference(): LlmInference? {
        if (!modelFile.exists()) {
            Log.i(TAG, "No local AI model found at ${modelFile.absolutePath}; falling back to heuristic parsing.")
            return null
        }

        return try {
            val options = LlmInferenceOptions.builder()
                .setModelPath(modelFile.absolutePath)
                .setMaxTokens(1024)
                .build()
            LlmInference.createFromOptions(applicationContext, options)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load local AI model.", e)
            null
        }
    }

    private fun buildPrompt(rawText: String): String = """
        You are given the raw OCR text scanned from a shopping receipt.
        Extract the store name, the purchase date, and every purchased line
        item exactly as abbreviated on the receipt, together with its
        quantity, unit price and line total price. Also extract the
        subtotal for each VAT/tax rate present (0% and 15%), the overall
        subtotal, the total tax amount and the final total. Nothing printed
        below the total is a purchased item - payment, change, rounding, the
        VAT summary table and the footer must all stay out of "items". The
        total tax is the sum of the VAT table's TAX column, never a
        VAT-inclusive amount. Format the date
        as ISO-8601 (yyyy-MM-dd). If no date is printed on the receipt, use
        null. If a line item's quantity or unit price cannot be determined,
        use null for that field only - never drop the item. Respond with
        ONLY a compact JSON object, no markdown, matching this exact schema:
        {"shopName": string|null, "date": string|null,
        "items": [{"name": string, "quantity": number|null, "unitPrice": number|null, "price": number}],
        "subtotalZeroRated": number|null, "subtotalStandardRated": number|null,
        "subtotal": number|null, "tax": number|null, "total": number|null}

        Receipt text:
        $rawText
    """.trimIndent()

    private fun parseModelResponse(response: String): ReceiptData? {
        val jsonText = extractJsonObject(response) ?: return null
        return try {
            val json = JSONObject(jsonText)
            val itemsArray = json.optJSONArray("items")
            val items = mutableListOf<ReceiptItem>()
            if (itemsArray != null) {
                for (i in 0 until itemsArray.length()) {
                    val itemJson = itemsArray.optJSONObject(i) ?: continue
                    val name = itemJson.optString("name").trim()
                    if (name.isEmpty() || !itemJson.has("price")) continue
                    items.add(
                        ReceiptItem(
                            name = name,
                            price = itemJson.optDouble("price"),
                            quantity = itemJson.optDoubleOrNull("quantity"),
                            unitPrice = itemJson.optDoubleOrNull("unitPrice")
                        )
                    )
                }
            }

            ReceiptData(
                shopName = json.optString("shopName").takeIf { it.isNotBlank() && it != "null" },
                date = parseIsoDate(json.optString("date")) ?: LocalDate.now(),
                items = items,
                subtotal = json.optDoubleOrNull("subtotal"),
                tax = json.optDoubleOrNull("tax"),
                total = json.optDoubleOrNull("total"),
                subtotalZeroRated = json.optDoubleOrNull("subtotalZeroRated"),
                subtotalStandardRated = json.optDoubleOrNull("subtotalStandardRated")
            )
        } catch (e: Exception) {
            Log.e(TAG, "Local AI response was not valid receipt JSON.", e)
            null
        }
    }

    private fun JSONObject.optDoubleOrNull(name: String): Double? =
        if (has(name) && !isNull(name)) optDouble(name) else null

    private fun parseIsoDate(value: String): LocalDate? =
        if (value.isBlank() || value == "null") {
            null
        } else {
            try {
                LocalDate.parse(value)
            } catch (e: DateTimeParseException) {
                null
            }
        }

    private fun extractJsonObject(text: String): String? {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start == -1 || end == -1 || end < start) return null
        return text.substring(start, end + 1)
    }

    /** Releases the underlying model resources. Call from `onDestroy`. */
    fun close() {
        llmInference?.close()
    }

    companion object {
        private const val TAG = "ReceiptAiParser"
        private const val MODEL_DIR = "llm"
        private const val MODEL_FILE_NAME = "model.task"
    }
}
