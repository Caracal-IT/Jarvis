package com.github.caracal.jarvis.groceries

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Looks up product information from multiple free barcode sources in a chain.
 * No API key required for any of the sources.
 *
 * Chain order:
 * 1. barcode-list.com  (HTML scrape — broad regional coverage including SA)
 * 2. Open Food Facts v2 (global food database)
 * 3. Open Food Facts .net mirror
 * 4. UPC Item DB (trial — 100 requests/day)
 * 5. Open Beauty Facts (non-food products)
 * 6. Shoprite ZA (HTML scrape — excellent SA coverage, last resort)
 */
object ProductLookupService {

    data class ProductInfo(
        val name: String,
        val category: String
    )

    suspend fun lookup(barcode: String): ProductInfo? =
        lookupBarcodeList(barcode)
            ?: lookupShoprite(barcode)
            ?: lookupOpenFoodFactsV2(barcode)
            ?: lookupOpenFoodFactsV0(barcode)
            ?: lookupUpcItemDb(barcode)
            ?: lookupOpenBeautyFacts(barcode)

    /**
     * Scrapes barcode-list.com — broad community database with good regional coverage.
     * Extracts the product name from the HTML <title> tag.
     * URL pattern: https://barcode-list.com/barcode/EN/barcode-{BARCODE}/Search.htm
     */
    private suspend fun lookupBarcodeList(barcode: String): ProductInfo? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://barcode-list.com/barcode/EN/barcode-$barcode/Search.htm")
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = 8000
                    readTimeout = 8000
                    setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 Chrome/120.0.0.0 Mobile Safari/537.36")
                    setRequestProperty("Accept", "text/html,application/xhtml+xml")
                    setRequestProperty("Accept-Language", "en-US,en;q=0.9")
                }
                if (connection.responseCode != HttpURLConnection.HTTP_OK) return@withContext null
                val html = connection.inputStream.bufferedReader(Charsets.UTF_8).readText()

                // Title format: "PRODUCT NAME - Barcode: 1234567890"
                val titleMatch = Regex("""<title>\s*(.+?)\s*-\s*Barcode""", RegexOption.IGNORE_CASE)
                    .find(html)
                val titleName = titleMatch?.groupValues?.get(1)?.trim()
                    ?.takeIf { it.isNotBlank() && !it.contains("search", ignoreCase = true) }

                if (titleName != null) {
                    // Guess category from keywords meta tag
                    val keywordsMatch = Regex("""<meta name="Keywords" content="([^"]+)"""", RegexOption.IGNORE_CASE)
                        .find(html)
                    val category = keywordsMatch?.groupValues?.get(1)?.lowercase() ?: ""
                    return@withContext ProductInfo(name = toTitleCase(titleName), category = category)
                }

                // Fallback: parse description meta for first product listed
                val descMatch = Regex("""following products:\s*([^;<"]+)""", RegexOption.IGNORE_CASE)
                    .find(html)
                val descName = descMatch?.groupValues?.get(1)?.trim()
                    ?.takeIf { it.isNotBlank() }
                if (descName != null) {
                    return@withContext ProductInfo(name = toTitleCase(descName), category = "")
                }

                null
            } catch (_: Exception) { null }
        }

    /**
     * Scrapes www.shoprite.co.za/search — excellent SA product coverage.
     * Products are embedded server-side as data-product-ga JSON attributes.
     * Picks the result whose name most closely matches the barcode search term.
     *
     * URL pattern: https://www.shoprite.co.za/search?q={BARCODE}
     */
    private suspend fun lookupShoprite(barcode: String): ProductInfo? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://www.shoprite.co.za/search?q=$barcode")
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = 10000
                    readTimeout = 10000
                    setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 Chrome/120.0.0.0 Mobile Safari/537.36")
                    setRequestProperty("Accept", "text/html,application/xhtml+xml")
                    setRequestProperty("Accept-Language", "en-ZA,en;q=0.9")
                    instanceFollowRedirects = true
                }
                if (connection.responseCode != HttpURLConnection.HTTP_OK) return@withContext null
                val html = connection.inputStream.bufferedReader(Charsets.UTF_8).readText()

                // Extract all data-product-ga JSON blobs
                val gaPattern = Regex("""data-product-ga='(\{[^']+\})'""")
                val names = gaPattern.findAll(html).mapNotNull { match ->
                    try {
                        val json = JSONObject(match.groupValues[1])
                        json.optString("name").takeIf { it.isNotBlank() }
                    } catch (_: Exception) { null }
                }.toList()

                if (names.isEmpty()) return@withContext null

                // Pick the best name: prefer the one with the most words (most descriptive)
                val bestName = names.maxByOrNull { it.split(" ").size } ?: names.first()

                // Derive category from the URL path embedded in the href (e.g. Milk%2C-Butter-and-Eggs)
                val categoryPattern = Regex("""href="/All-Departments/([^/]+)/""", RegexOption.IGNORE_CASE)
                val category = categoryPattern.find(html)?.groupValues?.get(1)
                    ?.replace("-", " ")?.lowercase() ?: ""

                ProductInfo(name = bestName.trim(), category = category)
            } catch (_: Exception) { null }
        }

    /** Converts ALL CAPS product names to Title Case for cleaner display. */
    private fun toTitleCase(input: String): String =
        input.lowercase().split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercaseChar() }
        }.trim()

    private suspend fun lookupOpenFoodFactsV2(barcode: String): ProductInfo? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://world.openfoodfacts.org/api/v2/product/$barcode.json?fields=product_name,categories_tags")
                val json = getJson(url) ?: return@withContext null
                if (json.optInt("status") != 1) return@withContext null
                extractOpenFoodFacts(json.optJSONObject("product"))
            } catch (_: Exception) { null }
        }

    private suspend fun lookupOpenFoodFactsV0(barcode: String): ProductInfo? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://world.openfoodfacts.net/api/v2/product/$barcode?fields=product_name,categories_tags")
                val json = getJson(url) ?: return@withContext null
                if (json.optInt("status") != 1) return@withContext null
                extractOpenFoodFacts(json.optJSONObject("product"))
            } catch (_: Exception) { null }
        }

    private suspend fun lookupUpcItemDb(barcode: String): ProductInfo? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.upcitemdb.com/prod/trial/lookup?upc=$barcode")
                val json = getJson(url) ?: return@withContext null
                val items = json.optJSONArray("items") ?: return@withContext null
                if (items.length() == 0) return@withContext null
                val item = items.getJSONObject(0)
                val name = item.optString("title").takeIf { it.isNotBlank() }
                    ?: return@withContext null
                val category = item.optString("category", "")
                ProductInfo(name = name.trim(), category = category)
            } catch (_: Exception) { null }
        }

    private suspend fun lookupOpenBeautyFacts(barcode: String): ProductInfo? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://world.openbeautyfacts.org/api/v0/product/$barcode.json?fields=product_name,categories_tags")
                val json = getJson(url) ?: return@withContext null
                if (json.optInt("status") != 1) return@withContext null
                extractOpenFoodFacts(json.optJSONObject("product"))
            } catch (_: Exception) { null }
        }

    private fun extractOpenFoodFacts(product: JSONObject?): ProductInfo? {
        product ?: return null
        val name = product.optString("product_name").takeIf { it.isNotBlank() }
            ?: return null
        val categoriesArray = product.optJSONArray("categories_tags")
        val category = (0 until (categoriesArray?.length() ?: 0))
            .map { categoriesArray!!.getString(it) }
            .firstOrNull { it.startsWith("en:") }
            ?.removePrefix("en:") ?: ""
        return ProductInfo(name = name.trim(), category = category)
    }


    private fun getJson(url: URL): JSONObject? {
        val connection = url.openConnection() as HttpURLConnection
        connection.apply {
            requestMethod = "GET"
            connectTimeout = 6000
            readTimeout = 6000
            setRequestProperty("User-Agent", "Jarvis-Android/1.0 (contact: jarvis@app.local)")
        }
        if (connection.responseCode != HttpURLConnection.HTTP_OK) return null
        val body = connection.inputStream.bufferedReader().readText()
        return JSONObject(body)
    }
}
