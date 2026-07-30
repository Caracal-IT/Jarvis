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
