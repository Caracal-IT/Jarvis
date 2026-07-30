package com.github.caracal.jarvis.postshopping

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Reproduces docs/examples/spar_slip.jpg as synthetic OCR line boxes - a
 * two-row-per-item layout for AQUARTZ (its "N @ unitPrice" quantity prints on
 * its own row below the name) and a single row per item for everything else -
 * to pin down the expected end-to-end result through
 * [ReceiptRowReconstructor] and then [ReceiptParser].
 */
class SparSlipEndToEndTest {

    @Test
    fun `spar slip reconstructs into the expected items and rounded total`() {
        val lines = listOf(
            PositionedLine(100, 130, 20, "AQUARTZ M/WATER"),
            PositionedLine(102, 128, 300, "5LT"),
            PositionedLine(140, 170, 20, "2 @ 32.99"),
            PositionedLine(143, 168, 320, "65.98 A"),

            PositionedLine(180, 210, 20, "CL.FRSH F/C MLK"),
            PositionedLine(182, 208, 250, "2L"),
            PositionedLine(183, 207, 320, "36.99 *"),

            PositionedLine(220, 250, 20, "JACOBS KRONUNG"),
            PositionedLine(222, 248, 250, "230GR"),
            PositionedLine(223, 247, 320, "124.99 A"),

            PositionedLine(260, 290, 20, "NESCAFE CLASSIC"),
            PositionedLine(262, 288, 250, "200GR"),
            PositionedLine(263, 287, 320, "104.99 A"),

            PositionedLine(300, 330, 20, "STA SOFT SPRING"),
            PositionedLine(302, 328, 250, "2LT"),
            PositionedLine(303, 327, 320, "74.99 A"),

            PositionedLine(340, 370, 20, "SPAR CARR VT1"),
            PositionedLine(342, 368, 250, "1'S"),
            PositionedLine(343, 367, 320, "1.10 A"),

            PositionedLine(380, 410, 20, "TOTAL"),
            PositionedLine(382, 408, 150, "FOR 7 ITEMS"),
            PositionedLine(383, 407, 320, "409.04"),

            PositionedLine(460, 490, 20, "ROUNDED TOTAL"),
            PositionedLine(463, 487, 320, "409.00")
        )

        val recognizedText = ReceiptRowReconstructor.reconstruct(lines)
        val receipt = ReceiptParser.parse(recognizedText)

        assertEquals(
            listOf(
                ReceiptItem("AQUARTZ M/WATER", 65.98, quantity = 2.0, unitPrice = 32.99),
                ReceiptItem("CL.FRSH F/C MLK", 36.99),
                ReceiptItem("JACOBS KRONUNG", 124.99),
                ReceiptItem("NESCAFE CLASSIC", 104.99),
                ReceiptItem("STA SOFT SPRING", 74.99),
                ReceiptItem("SPAR CARR VT1", 1.10)
            ),
            receipt.items
        )
        assertEquals(409.00, receipt.total)
    }
}
