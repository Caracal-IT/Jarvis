package com.github.caracal.jarvis.postshopping

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReceiptParserTest {

    @Test
    fun `parse links a two-line item name to its price on the following line, dropping its same-line size column`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            JACOBS KRONUNG        230GR
            124.99 A
            """.trimIndent()
        )

        assertEquals(listOf(ReceiptItem("JACOBS KRONUNG", 124.99)), receipt.items)
    }

    @Test
    fun `parse attaches a quantity line's quantity and unit price to the following item`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            AQUARTZ M/WATER        5LT
            2 @   32.99
            65.98 A
            """.trimIndent()
        )

        assertEquals(
            listOf(ReceiptItem("AQUARTZ M/WATER", 65.98, quantity = 2.0, unitPrice = 32.99)),
            receipt.items
        )
    }

    @Test
    fun `parse attaches quantity and price to the pending item when both share one OCR line`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            AQUARTZ M/WATER        5LT
            2   @   32.99           65.98 A
            """.trimIndent()
        )

        assertEquals(
            listOf(ReceiptItem("AQUARTZ M/WATER", 65.98, quantity = 2.0, unitPrice = 32.99)),
            receipt.items
        )
    }

    @Test
    fun `parse does not let an indented column fragment overwrite the pending item name`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            SPAR CARR VT1
                    1'S
            1.10 A
            """.trimIndent()
        )

        assertEquals(listOf(ReceiptItem("SPAR CARR VT1", 1.10)), receipt.items)
    }

    @Test
    fun `parse does not let a flush-left size fragment overwrite the pending item name`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            STA SOFT SPRING
            2LT
            74.99 A
            """.trimIndent()
        )

        assertEquals(listOf(ReceiptItem("STA SOFT SPRING", 74.99)), receipt.items)
    }

    @Test
    fun `parse keeps the pending item name when a size column shares the quantity's line`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            AQUARTZ M/WATER
            5LT      2 @ 32.99           65.98 A
            """.trimIndent()
        )

        assertEquals(
            listOf(ReceiptItem("AQUARTZ M/WATER", 65.98, quantity = 2.0, unitPrice = 32.99)),
            receipt.items
        )
    }

    @Test
    fun `parse reads name, quantity and price off a single merged item line`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            AQUARTZ M/WATER    5LT    2 @ 32.99      65.98 A
            """.trimIndent()
        )

        assertEquals(
            listOf(ReceiptItem("AQUARTZ M/WATER", 65.98, quantity = 2.0, unitPrice = 32.99)),
            receipt.items
        )
    }

    @Test
    fun `parse ignores amounts printed below the total, even beside footer text`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            JACOBS KRONUNG        230GR      124.99 A
            TOTAL     FOR 7 ITEMS            409.04
            ROUNDED TOTAL                    409.00
            THANK YOU FOR SHOPPING AT        372.05 A
            Sign up for SPAR REWARDS at       36.99 *
            """.trimIndent()
        )

        assertEquals(listOf(ReceiptItem("JACOBS KRONUNG", 124.99)), receipt.items)
        assertEquals(409.00, receipt.total)
    }

    @Test
    fun `parse reads tax off the vat table rather than the table's own heading`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            TOTAL     FOR 7 ITEMS            409.04
            -------- TAX INVOICE --------
            VAT rate    excl.     TAX      incl.
            0.00%       36.99     0.00     36.99 *
            15.00%     323.52    48.53    372.05 A
            """.trimIndent()
        )

        assertTrue(receipt.items.isEmpty())
        assertEquals(48.53, receipt.tax)
        assertEquals(36.99, receipt.subtotalZeroRated)
        assertEquals(323.52, receipt.subtotalStandardRated)
        assertEquals(360.51, receipt.subtotal)
    }

    @Test
    fun `parse keeps the pending item name when an amount column drifts in front of the price`() {
        // The photo is skewed, so the unit price of the row above lands on this row.
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            AQUARTZ M/WATER    5LT
            32.99                 65.98 A
            """.trimIndent()
        )

        assertEquals(listOf(ReceiptItem("AQUARTZ M/WATER", 65.98)), receipt.items)
    }

    @Test
    fun `parse reads a price whose VAT class OCR returned as a lookalike letter`() {
        // "А" here is Cyrillic (U+0410), which is what OCR returns for this slip's VAT class.
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            JACOBS KRONUNG        230GR      124.99 А
            """.trimIndent()
        )

        assertEquals(listOf(ReceiptItem("JACOBS KRONUNG", 124.99)), receipt.items)
    }

    @Test
    fun `parse recognizes a total whose keyword OCR misread with digits`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            JACOBS KRONUNG        230GR      124.99 A
            T0TAL     FOR 7 ITEMS            409.04
            """.trimIndent()
        )

        assertEquals(listOf(ReceiptItem("JACOBS KRONUNG", 124.99)), receipt.items)
        assertEquals(409.04, receipt.total)
    }

    @Test
    fun `parse does not read the header's VAT registration number as a tax label`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            TEL: 046 622 4737
            VAT: 4500296092
            AQUARTZ M/WATER    5LT    2 @ 32.99      65.98 A
            JACOBS KRONUNG     230GR                124.99 A
            """.trimIndent()
        )

        assertEquals(
            listOf(
                ReceiptItem("AQUARTZ M/WATER", 65.98, quantity = 2.0, unitPrice = 32.99),
                ReceiptItem("JACOBS KRONUNG", 124.99)
            ),
            receipt.items
        )
        assertNull(receipt.tax)
    }

    @Test
    fun `parse does not mistake a percentage-priced item for a vat table row`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            10% OFF COFFEE                    12.50
            JACOBS KRONUNG        230GR      124.99 A
            """.trimIndent()
        )

        assertEquals(
            listOf(
                ReceiptItem("10% OFF COFFEE", 12.50),
                ReceiptItem("JACOBS KRONUNG", 124.99)
            ),
            receipt.items
        )
    }

    @Test
    fun `parse keeps single-line name and price items working`() {
        val receipt = ReceiptParser.parse(
            """
            Corner Store
            Milk 2L 36.99
            Total 36.99
            """.trimIndent()
        )

        assertEquals(listOf(ReceiptItem("Milk 2L", 36.99)), receipt.items)
    }

    @Test
    fun `parse excludes tendered, change and rounding lines from items`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            TOTAL     FOR 7 ITEMS
            TENDERED Cash                  409.04
            CHANGE Cash                    410.00
            ROUNDING                         1.00
            ROUNDED TOTAL                    409.00
            """.trimIndent()
        )

        assertTrue(receipt.items.isEmpty())
        assertEquals(409.00, receipt.total)
    }

    @Test
    fun `parse does not turn a total row split across two lines into an item`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            TOTAL
            FOR 7 ITEMS            409.04
            """.trimIndent()
        )

        assertTrue(receipt.items.isEmpty())
        assertEquals(409.04, receipt.total)
    }

    @Test
    fun `parse does not let a stray tax-marker line become the next item's name`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            JACOBS KRONUNG        230GR
            124.99
            A
            NESCAFE CLASSIC       200GR
            104.99
            A
            """.trimIndent()
        )

        assertEquals(
            listOf(
                ReceiptItem("JACOBS KRONUNG", 124.99),
                ReceiptItem("NESCAFE CLASSIC", 104.99)
            ),
            receipt.items
        )
    }

    @Test
    fun `parse excludes numeric-only labels such as a VAT breakdown table`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            VAT rate    excl      TAX      incl.
            15.00%      323.52    48.53     372.05 A
            """.trimIndent()
        )

        assertTrue(receipt.items.isEmpty())
    }

    @Test
    fun `parse reads a dd MM yy date off the slip`() {
        val receipt = ReceiptParser.parse(
            """
            SPAR OAK COTTAGE
            SLIP / TILL / CASHIER / DATE / TIME
            6371   001   37   28.05.23  19:02
            """.trimIndent()
        )

        assertEquals(LocalDate.of(2023, 5, 28), receipt.date)
    }

    @Test
    fun `parse falls back to today when no date is present`() {
        val receipt = ReceiptParser.parse("Corner Store\nMilk 36.99")

        assertEquals(LocalDate.now(), receipt.date)
    }

    @Test
    fun `parse returns null shop name for empty text`() {
        val receipt = ReceiptParser.parse("")

        assertNull(receipt.shopName)
        assertTrue(receipt.items.isEmpty())
    }
}
