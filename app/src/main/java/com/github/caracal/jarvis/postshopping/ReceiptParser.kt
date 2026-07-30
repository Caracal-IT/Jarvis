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
 * column as its own OCR line (e.g. a lone "2LT"), or - on a photo of a
 * hand-held, curved slip - to land it on the neighbouring row instead of its
 * own. Unlike the same-line case, OCR does not preserve the original
 * indentation of a column split out this way, so it cannot be told apart from
 * a real item name by leading whitespace. A first column that is nothing but
 * a quantity/unit token (a number followed by a short unit, e.g. "5LT",
 * "230GR", "1'S") is therefore never accepted as an item name, leaving the
 * pending name from the row above in place.
 *
 * Only the part of the slip above the totals is scanned for items. From the
 * first row that is recognized as a total, a VAT/tax breakdown table or a tax
 * invoice heading, everything that follows is payment detail, tax summary and
 * marketing footer - and no amount printed there is ever a purchased item, no
 * matter how much it looks like one after OCR merges rows together.
 *
 * The tax amount is read out of the VAT breakdown table's TAX column rather
 * than off a keyword row: on a South African slip the table's own heading
 * ("VAT rate   excl.   TAX   incl.") contains the keyword while its amount
 * columns are on the following rows, so keyword matching alone picks up the
 * VAT-inclusive total of the first rate instead of the tax.
 */
object ReceiptParser {

    private val PRICE_LINE_REGEX = Regex("^(.*?)[\\s.:]*[\$R€£]?\\s*(-?\\d+[.,]\\d{2})\\s*[*A-Za-z]{0,2}$")
    private val QTY_LINE_REGEX = Regex("""^(\d+)\s*[@x]\s*(-?\d+[.,]\d{2})\s*[*A-Za-z]{0,2}$""", RegexOption.IGNORE_CASE)
    // Matches the "N @ unit price" part wherever it sits in front of a price, for
    // when the row's price column lands on the same OCR line as the quantity
    // instead of the next one (e.g. "2 @ 32.99   65.98 A"), which otherwise makes
    // the bare quantity digit look like the item's name.
    private val INLINE_QTY_REGEX = Regex("""(\d+)\s*[@x]\s*(-?\d+[.,]\d{2})""", RegexOption.IGNORE_CASE)
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
    private val TAX_INVOICE_HEADING_REGEX = Regex("""\b(tax|vat)\s+invoice\b""", RegexOption.IGNORE_CASE)
    /**
     * A store-header identifier line - a VAT registration ("VAT: 4500296092") or
     * phone number. It is complete in itself, so it is never held as a label
     * waiting for the price on the next row; doing so would let its "VAT" read as
     * the tax keyword for whatever amount happens to follow.
     */
    private val ID_LINE_REGEX = Regex("""\d{5,}""")

    /**
     * Cyrillic and Greek letters OCR returns in place of the Latin letters they
     * are drawn identically to - a receipt's VAT class "A" comes back as "А"
     * (U+0410) often enough to matter, and a line ending in one is not
     * recognized as a price at all.
     */
    private val CONFUSABLE_LETTERS: Map<Char, Char> =
        ("АВЕКМНОРСТУХЄаеорсухΑΒΕΖΗΙΚΜΝΟΡΤΥΧ" zip "ABEKMHOPCTYXEaeopcyxABEZHIKMNOPTYX").toMap()

    /** Digits OCR returns in place of the letters they are drawn like, e.g. "T0TAL" for "TOTAL". */
    private val CONFUSABLE_DIGITS = mapOf('0' to 'O', '1' to 'I', '5' to 'S', '8' to 'B')
    /** Such a digit only counts as a misread letter when it sits inside a word, not in an amount. */
    private val DIGIT_IN_WORD_REGEX = Regex("""(?<=\p{L})[0158]|[0158](?=\p{L})""")
    /** A VAT breakdown row: a rate ("0.00%", "15%") followed by its excl./tax/incl. amount columns. */
    private val VAT_TABLE_ROW_REGEX = Regex("""(\d+(?:[.,]\d+)?)\s*%(.*)""")
    private val AMOUNT_REGEX = Regex("""-?\d+[.,]\d{2}""")

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
        val vatTable = VatTable()
        var subtotal: Double? = null
        var tax: Double? = null
        var total: Double? = null
        var pendingLabel: String? = null
        var pendingQuantity: Double? = null
        var pendingUnitPrice: Double? = null
        /** True once the totals/tax/footer part of the slip starts; no items are printed below it. */
        var pastItems = false

        for (rawLine in lines) {
            val line = deconfuse(rawLine)
            val trimmedLine = line.trim()

            if (TAX_INVOICE_HEADING_REGEX.containsMatchIn(trimmedLine)) {
                pastItems = true
                pendingLabel = null
                continue
            }

            if (vatTable.consume(trimmedLine)) {
                pastItems = true
                pendingLabel = null
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
                if (TAX_MARKER_ONLY_REGEX.matches(trimmedLine)) continue

                val label = itemNameOf(line)
                if (label.isNotEmpty() && !ID_LINE_REGEX.containsMatchIn(label)) {
                    pendingLabel = label
                    pendingQuantity = null
                    pendingUnitPrice = null
                }
                continue
            }

            val price = match.groupValues[2].replace(',', '.').toDoubleOrNull() ?: continue
            // Anything the row printed in front of its price: the item's own name, and
            // possibly its "N @ unit price" quantity where OCR put both on one line.
            var beforePrice = match.groupValues[1]
            var quantity = pendingQuantity
            var unitPrice = pendingUnitPrice
            INLINE_QTY_REGEX.find(beforePrice)?.let { inlineQty ->
                quantity = inlineQty.groupValues[1].toDoubleOrNull()
                unitPrice = inlineQty.groupValues[2].replace(',', '.').toDoubleOrNull()
                beforePrice = beforePrice.take(inlineQty.range.first)
            }

            val inlineLabel = itemNameOf(beforePrice)
            val label = inlineLabel.ifEmpty { pendingLabel.orEmpty() }
            // A keyword row (e.g. "TOTAL") can itself be split across lines, with the
            // rest of its own text (e.g. "FOR 7 ITEMS") ending up as the inline label
            // of the following price line. Check both the pending and inline label so
            // that split keyword rows are still recognized instead of falling through
            // as a bogus item.
            val keywordCandidate = keywordTextOf(
                listOfNotNull(pendingLabel, firstColumn(beforePrice).ifEmpty { null }).joinToString(" ")
            )
            pendingLabel = null
            pendingQuantity = null
            pendingUnitPrice = null

            when {
                SUBTOTAL_REGEX.containsMatchIn(keywordCandidate) -> {
                    subtotal = price
                    pastItems = true
                }
                TOTAL_REGEX.containsMatchIn(keywordCandidate) -> {
                    total = price
                    pastItems = true
                }
                TAX_REGEX.containsMatchIn(keywordCandidate) -> {
                    tax = price
                    pastItems = true
                }
                label.isEmpty() -> Unit
                NOISE_LABEL_REGEX.containsMatchIn(keywordCandidate) -> Unit
                pastItems -> Unit
                else -> items.add(ReceiptItem(label, price, quantity, unitPrice))
            }
        }

        return ReceiptData(
            shopName = shopName,
            date = findDate(lines) ?: LocalDate.now(),
            items = items,
            subtotal = subtotal ?: vatTable.exclusiveTotal,
            tax = vatTable.taxTotal ?: tax,
            total = total,
            subtotalZeroRated = vatTable.zeroRated,
            subtotalStandardRated = vatTable.standardRated
        )
    }

    /**
     * The VAT breakdown table printed under a slip's totals, accumulated a row
     * at a time. Each row gives one VAT rate's exclusive amount, its tax and
     * its inclusive amount, so the slip's tax is the sum of the table's tax
     * column and its VAT-exclusive subtotal the sum of the excl. column.
     */
    private class VatTable {
        private var exclusiveSum = 0.0
        private var taxSum = 0.0
        private var rowCount = 0
        var zeroRated: Double? = null
            private set
        var standardRated: Double? = null
            private set

        val taxTotal: Double? get() = taxSum.takeIf { rowCount > 0 }
        val exclusiveTotal: Double? get() = exclusiveSum.takeIf { rowCount > 0 }

        /**
         * Reads [line] as a VAT table row, returning whether it was one. A rate
         * on its own is not enough - only a rate followed by a full set of
         * excl./tax/incl. amounts, so that an item priced off a "10% OFF"
         * promotion is not mistaken for the start of the tax summary.
         */
        fun consume(line: String): Boolean {
            val match = VAT_TABLE_ROW_REGEX.find(line) ?: return false
            val rate = match.groupValues[1].replace(',', '.').toDoubleOrNull() ?: return false
            val amounts = AMOUNT_REGEX.findAll(match.groupValues[2])
                .mapNotNull { it.value.replace(',', '.').toDoubleOrNull() }
                .toList()
            if (amounts.size < 3) return false

            val (exclusive, taxAmount) = amounts
            exclusiveSum += exclusive
            taxSum += taxAmount
            rowCount++
            if (rate == 0.0) zeroRated = exclusive else standardRated = exclusive
            return true
        }
    }

    /** The text before the first run of 2+ spaces (a column boundary), trimmed of stray label punctuation. */
    private fun firstColumn(text: String): String =
        text.split(COLUMN_SPLIT_REGEX).first().trim().trim('-', ':', '.', ' ')

    /**
     * [firstColumn], unless that column cannot name an item - either a bare
     * size/unit token ("5LT") or a fragment with no letters at all, which on a
     * skewed slip is usually a neighbouring row's amount column that drifted
     * across (e.g. the "32.99" of a "2 @ 32.99" quantity). Returning nothing
     * for those leaves the name printed above still standing.
     */
    private fun itemNameOf(text: String): String =
        firstColumn(text)
            .takeIf { it.any(Char::isLetter) && !SIZE_OR_UNIT_LINE_REGEX.matches(it) }
            .orEmpty()

    /** [text] with letters OCR reported as lookalike characters put back, so it can be matched by keyword. */
    private fun deconfuse(text: String): String =
        if (text.all { it.code < 128 }) text else text.map { CONFUSABLE_LETTERS[it] ?: it }.joinToString("")

    /** [text] as a keyword candidate: digits standing in for letters within a word are read back as letters. */
    private fun keywordTextOf(text: String): String =
        DIGIT_IN_WORD_REGEX.replace(text) { CONFUSABLE_DIGITS.getValue(it.value[0]).toString() }

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
