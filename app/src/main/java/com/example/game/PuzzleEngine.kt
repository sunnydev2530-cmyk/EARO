package com.example.game

import com.example.model.ArrowPiece

object PuzzleEngine {

    /**
     * Returns all grid coordinates strictly ahead of [arrow] along its pointing direction
     * up to the boundary of a [rows] x [cols] board.
     */
    fun rayCells(arrow: ArrowPiece, rows: Int, cols: Int): List<Pair<Int, Int>> {
        val cells = ArrayList<Pair<Int, Int>>(maxOf(rows, cols))
        var r = arrow.row + arrow.direction.dRow
        var c = arrow.col + arrow.direction.dCol
        while (r in 0 until rows && c in 0 until cols) {
            cells.add(r to c)
            r += arrow.direction.dRow
            c += arrow.direction.dCol
        }
        return cells
    }

    /**
     * Checks whether [arrow] has an unobstructed straight-line path to exit the board.
     */
    fun canArrowEscape(
        arrow: ArrowPiece,
        activeArrows: List<ArrowPiece>,
        rows: Int,
        cols: Int
    ): Boolean = findBlockingArrow(arrow, activeArrows, rows, cols) == null

    /**
     * Returns the first [ArrowPiece] blocking [arrow]'s exit path, or null if clear.
     */
    fun findBlockingArrow(
        arrow: ArrowPiece,
        activeArrows: List<ArrowPiece>,
        rows: Int,
        cols: Int
    ): ArrowPiece? {
        val occupied = HashMap<Pair<Int, Int>, ArrowPiece>(activeArrows.size)
        for (other in activeArrows) {
            if (other.id != arrow.id) {
                occupied[other.row to other.col] = other
            }
        }
        for (cell in rayCells(arrow, rows, cols)) {
            val blocker = occupied[cell]
            if (blocker != null) {
                return blocker
            }
        }
        return null
    }

    /**
     * Returns all currently movable arrows on the board.
     */
    fun findValidMoves(
        activeArrows: List<ArrowPiece>,
        rows: Int,
        cols: Int
    ): List<ArrowPiece> {
        val occupied = HashMap<Pair<Int, Int>, ArrowPiece>(activeArrows.size)
        for (piece in activeArrows) {
            occupied[piece.row to piece.col] = piece
        }
        return activeArrows.filter { arrow ->
            rayCells(arrow, rows, cols).none { cell -> occupied.containsKey(cell) }
        }
    }

    /**
     * Computes a guaranteed valid move from the current board state for the Hint system.
     * Prioritizes moves that unblock the largest number of remaining arrows.
     */
    fun findHintArrow(
        activeArrows: List<ArrowPiece>,
        rows: Int,
        cols: Int
    ): ArrowPiece? {
        val validMoves = findValidMoves(activeArrows, rows, cols)
        if (validMoves.isEmpty()) return null

        val rayCache = activeArrows.associate { it.id to rayCells(it, rows, cols).toSet() }
        return validMoves.maxByOrNull { candidate ->
            val candidateCell = candidate.row to candidate.col
            var unblockedCount = 0
            for (other in activeArrows) {
                if (other.id != candidate.id && candidateCell in (rayCache[other.id] ?: emptySet())) {
                    unblockedCount++
                }
            }
            unblockedCount
        }
    }

    /**
     * Verifies via topological peeling that all [arrows] can escape without cycles or deadlocks.
     */
    fun isBoardSolvable(
        arrows: List<ArrowPiece>,
        rows: Int,
        cols: Int
    ): Boolean {
        val remaining = arrows.toMutableList()
        while (remaining.isNotEmpty()) {
            val valid = findValidMoves(remaining, rows, cols)
            if (valid.isEmpty()) return false
            val validIds = valid.map { it.id }.toSet()
            remaining.removeAll { it.id in validIds }
        }
        return true
    }

    /**
     * Calculates 1-3 star rating based on blocked mistakes and hint usage.
     */
    fun calculateStars(blockedAttempts: Int, hintsUsed: Int): Int {
        return when {
            blockedAttempts == 0 && hintsUsed == 0 -> 3
            blockedAttempts <= 2 && hintsUsed <= 1 -> 2
            else -> 1
        }
    }
}
