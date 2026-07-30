package com.github.caracal.jarvis.postshopping

/**
 * A single OCR-recognized line of text with its bounding box, expressed as
 * the plain integers ML Kit's `android.graphics.Rect` exposes - kept
 * independent of any Android/ML Kit type so [ReceiptRowReconstructor] can be
 * unit tested on the JVM without an Android runtime.
 */
data class PositionedLine(val top: Int, val bottom: Int, val left: Int, val text: String)

/**
 * Reassembles OCR lines into printed receipt rows using their bounding boxes,
 * rather than trusting ML Kit's own line/block grouping.
 *
 * ML Kit groups recognized text into blocks and lines based on proximity, but
 * a receipt's wide gap between a left-aligned description column and a
 * right-aligned price column is exactly the kind of gap that can make ML Kit
 * report the two columns as separate lines (or separate blocks) despite them
 * being printed on the same physical row. Sorting purely by each line's own
 * vertical position does not reliably fix this either, since two lines on the
 * same printed row are not guaranteed to report identical bounding-box tops.
 *
 * Instead, lines are grouped into rows by vertical (top/bottom) overlap, then
 * ordered left-to-right within each row and joined with a run of spaces wide
 * enough to be read back as a column boundary by [ReceiptParser]. This turns
 * a same-row split such as "2 @ 32.99" / "65.98 A" back into a single line,
 * "2 @ 32.99   65.98 A", the way a single-column receipt would have produced
 * it in the first place - regardless of how ML Kit happened to split it.
 */
object ReceiptRowReconstructor {

    /** Column boundary width injected between same-row fragments; must be >= [ReceiptParser]'s column-split threshold. */
    private const val COLUMN_GAP = "   "

    fun reconstruct(lines: List<PositionedLine>): String {
        val rows = mutableListOf<MutableList<PositionedLine>>()

        for (line in lines.sortedBy { it.top }) {
            val row = rows.lastOrNull()?.takeIf { it.sharesRowWith(line) }
            if (row != null) row.add(line) else rows.add(mutableListOf(line))
        }

        return rows.joinToString("\n") { row ->
            row.sortedBy { it.left }.joinToString(COLUMN_GAP) { it.text }
        }
    }

    /**
     * True when [line] vertically overlaps this row by more than half of its own height.
     *
     * Compares against the row's first line only, not a running min/max of every
     * member added so far - an accumulated envelope grows with each merge, and on
     * a receipt's tightly and consistently spaced lines that growth snowballs into
     * merging the entire receipt into a single row.
     */
    private fun List<PositionedLine>.sharesRowWith(line: PositionedLine): Boolean {
        val anchor = first()
        val overlap = minOf(anchor.bottom, line.bottom) - maxOf(anchor.top, line.top)
        val lineHeight = line.bottom - line.top
        return lineHeight > 0 && overlap > lineHeight / 2
    }
}
