import util.prcp
import util.readInput
import util.rowCols


fun main() {
    fun part1(input: List<String>): Long {
        val matrixPart = input.slice(0..<input.lastIndex)
        val operators = input[input.lastIndex].split(Regex("\\s+"))

        val matrix = matrixPart.map { it.trim().split(Regex("\\s+")).map { it.toLong() } }
        return operators.mapIndexed { col: Int, op ->
            val values = matrix.map { it[col] }
            if (op == "+") {
                val sum = values.sum()
                return@mapIndexed sum
            } else {
                val multiplied = values.reduce { acc, next -> acc * next }
                return@mapIndexed multiplied
            }
        }.sum()
    }

    fun part2(input: List<String>): Long {
        TODO()
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06.test")
    check(part1(testInput) == 4277556L)
    val input = readInput("Day06")
    prcp(part1(input))


//    check(part2(testInput) == 14L)
//    check(part2(testInput + testInput) == 14L)
//    prcp(part2(input))
}
