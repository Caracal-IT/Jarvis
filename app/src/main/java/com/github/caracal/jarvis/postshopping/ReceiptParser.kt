package com.github.caracal.jarvis.postshopping

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * A single purchased line item parsed from a receipt.
 *
 * [quantity] and [unitPrice] are populated when a "N @ unit price" line
 * precedes the item's price line; otherwise they are `null`.
 */
data class ReceiptItem(
    val name: String,
    val price: Double,
    val quantity: Double? = null,
    val unitPrice: Double? = null
)

/**
 * Structured data extracted from the raw OCR text of a receipt.
 *
 * [subtotalZeroRated] and [subtotalStandardRated] break [subtotal] down by
 * VAT rate (0% and 15%, per South African receipts) when that detail is
 * available - currently only from [ReceiptAiParser].
 */
data class ReceiptData(
    val shopName: String?,
    val date: LocalDate,
    val items: List<ReceiptItem>,
    val subtotal: Double?,
    val tax: Double?,
    val total: Double?,
    val subtotalZeroRated: Double? = null,
    val subtotalStandardRated: Double? = null
)

/**
 * Parses the raw text recognized by ML Kit off a scanned receipt into a
 * structured [ReceiptData] using line-based heuristics: the shop name is
 * assumed to be the first non-empty line, subtotal/tax/total are matched by
 * keyword, and the purchase date is matched against a handful of common date
 * formats. If no date can be read off the slip, today's date is used
 * instead.
 *
 * Purchased items are trickier: many receipts print the item's name/size on
 * one physical line and its price - often followed by a single-letter VAT
 * class (e.g. "124.99 A") - on the next, because of the wide gap between the
 * left-aligned description and the right-aligned price column. So a price
 * line with nothing usable in front of it inherits the most recent
 * non-price line as its name, while a "N @ unit price" line is captured as
 * that item's quantity/unit price rather than treated as a distinct line
 * item.
 *
 * A run of more than one space is treated as a column boundary, and only the
 * first column is ever used as the item name - this drops a same-line
 * size/unit column (e.g. "AQUARTZ M/WATER        5LT" names only "AQUARTZ
 * M/WATER"). That wide gap can also cause ML Kit to report the size/unit
 * column as its own OCR line (e.g. a lone "2LT"); unlike the same-line case,
 * OCR does not preserve the original indentation of a line split out this
 * way, so it cannot be told apart from a real item name by leading
 * whitespace. It is instead recognized by looking like a bare quantity/unit
 * token (a number followed by a short unit, e.g. "5LT", "230GR", "1'S") and
 * skipped without disturbing the pending item name.
 */
object ReceiptParser {

    private val PRICE_LINE_REGEX = Regex("^(.*?)[\\s.:]*[\$R€£]?\\s*(-?\\d+[.,]\\d{2})\\s*[*A-Za-z]{0,2}$")
    private val QTY_LINE_REGEX = Regex("""^(\d+)\s*[@x]\s*(-?\d+[.,]\d{2})\s*[*A-Za-z]{0,2}$""", RegexOption.IGNORE_CASE)
    // Same as QTY_LINE_REGEX, but for when the row's price column lands on the same
    // OCR line as the quantity instead of the next one (e.g. "2 @ 32.99   65.98 A"),
    // which otherwise makes PRICE_LINE_REGEX mistake the bare quantity digit for the
    // item's name and drop the row for having no letters in it.
    private val QTY_PRICE_LINE_REGEX = Regex(
        """^(\d+)\s*[@x]\s*(-?\d+[.,]\d{2})[\s.:]*[${'$'}R€£]?\s*(-?\d+[.,]\d{2})\s*[*A-Za-z]{0,2}$""",
        RegexOption.IGNORE_CASE
    )
    private val COLUMN_SPLIT_REGEX = Regex("""\s{2,}""")
    private val SIZE_OR_UNIT_LINE_REGEX = Regex(
        """^\d+(?:[.,]\d+)?'?\s*(ML|LT|L|GR|G|KG|MG|CL|CM|MM|EA|PK|CT|PCS|PC|S|X)$""",
        RegexOption.IGNORE_CASE
    )
    private val TAX_MARKER_ONLY_REGEX = Regex("""^[*A-Za-z]{1,2}$""")
    private val SUBTOTAL_REGEX = Regex("""sub[\s-]?total""", RegexOption.IGNORE_CASE)
    private val TAX_REGEX = Regex("""\b(tax|vat|gst)\b""", RegexOption.IGNORE_CASE)
    private val TOTAL_REGEX = Regex("""\btotal\b""", RegexOption.IGNORE_CASE)
    private val NOISE_LABEL_REGEX = Regex(
        """\b(tendered|change|rounding|cash|card|balance)\b""",
        RegexOption.IGNORE_CASE
    )

    private val DATE_REGEX = Regex(
        """\b(\d{1,2}[/.-]\d{1,2}[/.-]\d{2,4}|\d{4}[/.-]\d{1,2}[/.-]\d{1,2})\b"""
    )

    // All matched against the date candidate after its separators are
    // normalized to '/', so every pattern here uses '/' too.
    private val DATE_FORMATTERS = listOf(
        "yyyy/MM/dd",
        "dd/MM/yyyy",
        "MM/dd/yyyy",
        "dd/MM/yy",
        "MM/dd/yy"
    ).map { DateTimeFormatter.ofPattern(it) }

    fun parse(rawText: String): ReceiptData {
        val lines = rawText.lines().map { it.trimEnd() }.filter { it.isNotBlank() }

        val shopName = lines.firstOrNull { line -> !PRICE_LINE_REGEX.matches(line.trim()) }?.trim()

        val items = mutableListOf<ReceiptItem>()
        var subtotal: Double? = null
        var tax: Double? = null
        var total: Double? = null
        var pendingLabel: String? = null
        var pendingQuantity: Double? = null
        var pendingUnitPrice: Double? = null

        for (line in lines) {
            val trimmedLine = line.trim()

            val qtyPriceMatch = QTY_PRICE_LINE_REGEX.matchEntire(trimmedLine)
            if (qtyPriceMatch != null) {
                val quantity = qtyPriceMatch.groupValues[1].toDoubleOrNull()
                val unitPrice = qtyPriceMatch.groupValues[2].replace(',', '.').toDoubleOrNull()
                val price = qtyPriceMatch.groupValues[3].replace(',', '.').toDoubleOrNull()
                val label = pendingLabel.orEmpty()
                pendingLabel = null
                pendingQuantity = null
                pendingUnitPrice = null

                if (price != null) {
                    when {
                        SUBTOTAL_REGEX.containsMatchIn(label) -> subtotal = price
                        TOTAL_REGEX.containsMatchIn(label) -> total = price
                        TAX_REGEX.containsMatchIn(label) -> tax = price
                        label.isEmpty() -> Unit
                        NOISE_LABEL_REGEX.containsMatchIn(label) -> Unit
                        !label.any { it.isLetter() } -> Unit
                        else -> items.add(ReceiptItem(label, price, quantity, unitPrice))
                    }
                }
                continue
            }

            val qtyMatch = QTY_LINE_REGEX.matchEntire(trimmedLine)
            if (qtyMatch != null) {
                pendingQuantity = qtyMatch.groupValues[1].toDoubleOrNull()
                pendingUnitPrice = qtyMatch.groupValues[2].replace(',', '.').toDoubleOrNull()
                continue
            }

            val match = PRICE_LINE_REGEX.matchEntire(trimmedLine)
            if (match == null) {
                if (SIZE_OR_UNIT_LINE_REGEX.matches(trimmedLine)) continue
                if (TAX_MARKER_ONLY_REGEX.matches(trimmedLine)) continue

                val label = firstColumn(line)
                if (label.isNotEmpty()) {
                    pendingLabel = label
                    pendingQuantity = null
                    pendingUnitPrice = null
                }
                continue
            }

            val inlineLabel = firstColumn(match.groupValues[1])
            val price = match.groupValues[2].replace(',', '.').toDoubleOrNull() ?: continue
            val label = inlineLabel.ifEmpty { pendingLabel.orEmpty() }
            // A keyword row (e.g. "TOTAL") can itself be split across lines, with the
            // rest of its own text (e.g. "FOR 7 ITEMS") ending up as the inline label
            // of the following price line. Check both the pending and inline label so
            // that split keyword rows are still recognized instead of falling through
            // as a bogus item.
            val keywordCandidate = listOfNotNull(pendingLabel, inlineLabel.ifEmpty { null })
                .joinToString(" ")
            pendingLabel = null
            val quantity = pendingQuantity
            val unitPrice = pendingUnitPrice
            pendingQuantity = null
            pendingUnitPrice = null

            when {
                SUBTOTAL_REGEX.containsMatchIn(keywordCandidate) -> subtotal = price
                TOTAL_REGEX.containsMatchIn(keywordCandidate) -> total = price
                TAX_REGEX.containsMatchIn(keywordCandidate) -> tax = price
                label.isEmpty() -> Unit
                NOISE_LABEL_REGEX.containsMatchIn(keywordCandidate) -> Unit
                !label.any { it.isLetter() } -> Unit
                else -> items.add(ReceiptItem(label, price, quantity, unitPrice))
            }
        }

        return ReceiptData(
            shopName = shopName,
            date = findDate(lines) ?: LocalDate.now(),
            items = items,
            subtotal = subtotal,
            tax = tax,
            total = total
        )
    }

    /** The text before the first run of 2+ spaces (a column boundary), trimmed of stray label punctuation. */
    private fun firstColumn(text: String): String =
        text.split(COLUMN_SPLIT_REGEX).first().trim().trim('-', ':', '.', ' ')

    private fun findDate(lines: List<String>): LocalDate? {
        for (line in lines) {
            val candidate = DATE_REGEX.find(line)?.value ?: continue
            parseDate(candidate)?.let { return it }
        }
        return null
    }

    private fun parseDate(candidate: String): LocalDate? {
        val normalized = candidate.replace('.', '/').replace('-', '/')
        for (formatter in DATE_FORMATTERS) {
            try {
                return LocalDate.parse(normalized, formatter)
            } catch (e: DateTimeParseException) {
                // Try the next candidate format.
            }
        }
        return null
    }
}
