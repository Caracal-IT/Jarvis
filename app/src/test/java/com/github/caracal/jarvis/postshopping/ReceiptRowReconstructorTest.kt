package com.github.caracal.jarvis.postshopping

import org.junit.Assert.assertEquals
import org.junit.Test

class ReceiptRowReconstructorTest {

    @Test
    fun `reconstruct merges a description and its same-row price into one line`() {
        val lines = listOf(
            PositionedLine(top = 100, bottom = 130, left = 20, text = "AQUARTZ M/WATER"),
            PositionedLine(top = 102, bottom = 128, left = 300, text = "5LT"),
            PositionedLine(top = 140, bottom = 170, left = 20, text = "2 @ 32.99"),
            PositionedLine(top = 143, bottom = 168, left = 320, text = "65.98 A")
        )

        val result = ReceiptRowReconstructor.reconstruct(lines)

        assertEquals(
            "AQUARTZ M/WATER   5LT\n2 @ 32.99   65.98 A",
            result
        )
    }

    @Test
    fun `reconstruct keeps rows with no vertical overlap on separate lines in top-to-bottom order`() {
        val lines = listOf(
            PositionedLine(top = 200, bottom = 230, left = 20, text = "NESCAFE CLASSIC"),
            PositionedLine(top = 100, bottom = 130, left = 20, text = "AQUARTZ M/WATER")
        )

        val result = ReceiptRowReconstructor.reconstruct(lines)

        assertEquals("AQUARTZ M/WATER\nNESCAFE CLASSIC", result)
    }

    @Test
    fun `reconstruct orders same-row fragments left to right regardless of input order`() {
        val lines = listOf(
            PositionedLine(top = 100, bottom = 130, left = 300, text = "65.98"),
            PositionedLine(top = 100, bottom = 130, left = 20, text = "AQUARTZ M/WATER")
        )

        val result = ReceiptRowReconstructor.reconstruct(lines)

        assertEquals("AQUARTZ M/WATER   65.98", result)
    }

    @Test
    fun `reconstruct does not merge rows whose vertical overlap is less than half a line's height`() {
        val lines = listOf(
            PositionedLine(top = 100, bottom = 130, left = 20, text = "JACOBS KRONUNG"),
            // Overlaps the row above by only 5px out of a 30px line height.
            PositionedLine(top = 125, bottom = 155, left = 300, text = "124.99 A")
        )

        val result = ReceiptRowReconstructor.reconstruct(lines)

        assertEquals("JACOBS KRONUNG\n124.99 A", result)
    }

    @Test
    fun `reconstruct returns empty string for no lines`() {
        assertEquals("", ReceiptRowReconstructor.reconstruct(emptyList()))
    }

    @Test
    fun `reconstruct does not cascade-merge many tightly and consistently spaced rows into one`() {
        // A realistic receipt: ~30px line height with only a few px gap between rows,
        // repeated many times. An envelope that grows with every merge would eventually
        // overlap every subsequent row; a fixed per-row anchor must not.
        val lines = (0 until 10).map { i ->
            val top = i * 33
            PositionedLine(top = top, bottom = top + 30, left = 20, text = "LINE $i")
        }

        val result = ReceiptRowReconstructor.reconstruct(lines)

        assertEquals((0 until 10).joinToString("\n") { "LINE $it" }, result)
    }
}
