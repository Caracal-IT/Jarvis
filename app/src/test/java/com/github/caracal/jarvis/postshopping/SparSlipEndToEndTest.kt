package com.github.caracal.jarvis.postshopping

import java.time.LocalDate
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

    /**
     * The whole slip, not just its item block: the store header, the payment
     * block below the total, the VAT breakdown table and the marketing footer
     * are all printed on the same receipt, and none of them may leak into the
     * items or the tax amount.
     */
    @Test
    fun `spar slip ignores everything printed after the total and reads tax off the vat table`() {
        var top = 100
        fun row(vararg columns: Pair<Int, String>): List<PositionedLine> {
            val rowTop = top
            top += 40
            return columns.map { (left, text) -> PositionedLine(rowTop, rowTop + 30, left, text) }
        }

        val lines = listOf(
            row(20 to "SPAR"),
            row(20 to "WELCOME TO"),
            row(20 to "SPAR OAK COTTAGE"),
            row(20 to "TEL: 046 622 4737"),
            row(20 to "VAT: 4500296092"),
            row(330 to "R"),

            row(20 to "AQUARTZ M/WATER", 250 to "5LT"),
            row(60 to "2 @ 32.99", 320 to "65.98 A"),
            row(20 to "CL.FRSH F/C MLK", 250 to "2L", 320 to "36.99 *"),
            row(20 to "JACOBS KRONUNG", 250 to "230GR", 310 to "124.99 A"),
            row(20 to "NESCAFE CLASSIC", 250 to "200GR", 310 to "104.99 A"),
            row(20 to "STA SOFT SPRING", 250 to "2LT", 320 to "74.99 A"),
            row(20 to "SPAR CARR VT1", 250 to "1'S", 320 to "1.10 A"),

            row(20 to "TOTAL", 150 to "FOR 7 ITEMS", 320 to "409.04"),
            row(20 to "TENDERED Cash", 320 to "410.00"),
            row(20 to "CHANGE  Cash", 320 to "1.00"),
            row(20 to "ROUNDING", 320 to "0.04"),
            row(20 to "ROUNDED TOTAL", 320 to "409.00"),

            row(20 to "-------- TAX INVOICE --------"),
            row(20 to "VAT rate", 140 to "excl.", 240 to "TAX", 330 to "incl."),
            row(20 to "0.00%", 140 to "36.99", 240 to "0.00", 330 to "36.99 *"),
            row(20 to "15.00%", 140 to "323.52", 240 to "48.53", 330 to "372.05 A"),

            row(20 to "Hi. Thank you for using SPAR Rewards."),
            row(20 to "SLIP / TILL / CASHIER / DATE / TIME"),
            row(20 to "6371", 120 to "001", 200 to "37", 260 to "28.05.23", 360 to "19:02"),
            row(20 to "CASHIER NAME: SIPHOKAZI..."),
            row(20 to "THANK YOU FOR SHOPPING AT"),
            row(20 to "SPAR OAK COTTAGE"),
            row(20 to "FIND MORE DEALS ON OUR FACE BOOK PAGE"),
            row(20 to "https://www.facebook.com/SPARoakCottage/"),
            row(20 to "OPEN 24 HOURS ON FRIDAY & SATURDAY NIGHTS"),
            row(20 to "Sign up for SPAR REWARDS at"),
            row(20 to "https://me.spar.co.za")
        ).flatten()

        val receipt = ReceiptParser.parse(ReceiptRowReconstructor.reconstruct(lines))

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
        assertEquals(48.53, receipt.tax)
        assertEquals(36.99, receipt.subtotalZeroRated)
        assertEquals(323.52, receipt.subtotalStandardRated)
        assertEquals(360.51, receipt.subtotal)
        assertEquals(LocalDate.of(2023, 5, 28), receipt.date)
    }
}
