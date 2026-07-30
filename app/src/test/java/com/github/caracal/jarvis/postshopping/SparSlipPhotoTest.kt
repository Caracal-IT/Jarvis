package com.github.caracal.jarvis.postshopping

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Runs the pipeline over a real OCR read of docs/examples/spar_slip.jpg -
 * `spar_slip_ocr.tsv` holds one recognized line per row as
 * `top<TAB>bottom<TAB>left<TAB>text`, captured from that photo.
 *
 * The synthetic fixtures in [SparSlipEndToEndTest] assume a slip that was
 * scanned flat. This one is a photo of a slip held in a hand: it is skewed
 * enough that the right-hand price column of every row sits about ten pixels
 * higher than the description it belongs to, some columns are dropped or
 * merged, and OCR substitutes lookalike characters (the VAT class "A" comes
 * back as a Cyrillic "А"). Those are the conditions the parser actually has
 * to survive, so it is worth asserting against them directly.
 */
class SparSlipPhotoTest {

    private fun ocrLines(): List<PositionedLine> =
        checkNotNull(javaClass.classLoader?.getResourceAsStream("spar_slip_ocr.tsv")) {
            "spar_slip_ocr.tsv missing from test resources"
        }.bufferedReader().useLines { lines ->
            lines.filter { it.isNotBlank() }.map { line ->
                val (top, bottom, left, text) = line.split("\t", limit = 4)
                PositionedLine(top.toInt(), bottom.toInt(), left.toInt(), text)
            }.toList()
        }

    private operator fun <T> List<T>.component4(): T = this[3]

    @Test
    fun `photo of the spar slip parses into its six items, totals and tax`() {
        val receipt = ReceiptParser.parse(ReceiptRowReconstructor.reconstruct(ocrLines()))

        assertEquals(
            listOf("AQUARTZ M/WATER", "CL.FRSH F/C MLK", "JACOBS KRONUNG", "NESCAFE CLASSIC", "STA SOFT SPRING", "SPAR CARR VT1"),
            receipt.items.map { it.name }
        )
        assertEquals(
            listOf(65.98, 36.99, 124.99, 104.99, 74.99, 1.10),
            receipt.items.map { it.price }
        )
        assertEquals(409.00, receipt.total)
        assertEquals(48.53, receipt.tax)
        assertEquals(36.99, receipt.subtotalZeroRated)
        assertEquals(323.52, receipt.subtotalStandardRated)
    }
}
