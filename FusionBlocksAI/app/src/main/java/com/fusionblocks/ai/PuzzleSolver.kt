package com.fusionblocks.ai

/**
 * Exhaustive three-piece search for a standard block-placement board.
 * Board is a BooleanArray in row-major order; true means occupied.
 * Shapes are lists of (x,y) offsets. No rotations are assumed unless the game supplies rotated shapes.
 */
object PuzzleSolver {
    data class Shape(val cells: List<Pair<Int, Int>>, val sourceIndex: Int)
    data class Move(val shapeIndex: Int, val x: Int, val y: Int, val clearedLines: Int)
    data class Result(val moves: List<Move>, val score: Int, val finalBoard: BooleanArray)

    fun solve(board: BooleanArray, width: Int, height: Int, shapes: List<Shape>): Result? {
        require(board.size == width * height)
        if (shapes.size != 3) return null
        var best: Result? = null
        fun search(current: BooleanArray, remaining: List<Shape>, moves: List<Move>, score: Int) {
            if (remaining.isEmpty()) {
                if (best == null || score > best!!.score) best = Result(moves, score, current)
                return
            }
            for (shape in remaining) {
                val nextRemaining = remaining.filterIndexed { _, s -> s !== shape }
                for (y in 0 until height) for (x in 0 until width) {
                    if (!fits(current, width, height, shape, x, y)) continue
                    val placed = current.copyOf()
                    shape.cells.forEach { (sx, sy) -> placed[(y + sy) * width + x + sx] = true }
                    val (cleared, lines) = clearFullLines(placed, width, height)
                    val move = Move(shape.sourceIndex, x, y, lines)
                    // Strongly prefer clears; small preference for keeping the board open.
                    val value = score + lines * 1000 + shape.cells.size * 2 + emptyNeighbourScore(cleared, width, height)
                    search(cleared, nextRemaining, moves + move, value)
                }
            }
        }
        search(board.copyOf(), shapes, emptyList(), 0)
        return best
    }

    private fun fits(board: BooleanArray, w: Int, h: Int, s: Shape, x: Int, y: Int): Boolean =
        s.cells.all { (sx, sy) -> x + sx in 0 until w && y + sy in 0 until h && !board[(y + sy) * w + x + sx] }

    private fun clearFullLines(input: BooleanArray, w: Int, h: Int): Pair<BooleanArray, Int> {
        val b = input.copyOf(); val rows = (0 until h).filter { y -> (0 until w).all { x -> b[y * w + x] } }
        val cols = (0 until w).filter { x -> (0 until h).all { y -> b[y * w + x] } }
        rows.forEach { y -> for (x in 0 until w) b[y * w + x] = false }
        cols.forEach { x -> for (y in 0 until h) b[y * w + x] = false }
        return b to (rows.size + cols.size)
    }

    private fun emptyNeighbourScore(b: BooleanArray, w: Int, h: Int): Int {
        var score = 0
        for (y in 0 until h) for (x in 0 until w) if (!b[y * w + x]) {
            var n = 0
            if (x > 0 && b[y * w + x - 1]) n++
            if (x + 1 < w && b[y * w + x + 1]) n++
            if (y > 0 && b[(y - 1) * w + x]) n++
            if (y + 1 < h && b[(y + 1) * w + x]) n++
            if (n == 0) score++
        }
        return score
    }
}
