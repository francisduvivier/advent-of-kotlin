import util.prcp
import util.readInput

fun getBestJoltage(input: String, amount: Int = 2): String {
    if(amount == 0) return ""
    val numbersChars = input.split("").slice(1..input.length)
    val max1 = numbersChars.slice(0..<numbersChars.size - (amount-1)).maxBy { it.toInt() }
    val rest = numbersChars.slice((numbersChars.indexOf(max1))+1..<numbersChars.size)
    return max1 +getBestJoltage(rest.joinToString(""), amount-1)
}

fun main() {
    fun part1(input: List<String>): Long {
        return input.map { getBestJoltage(it).toLong() }.sumOf { it }
    }

    fun part2(input: List<String>): Long {
        return input.map { getBestJoltage(it, 12).toLong() }.sumOf { it }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03.test")
    check(part1(testInput) == 357L)
    val input = readInput("Day03")
    prcp(part1(input))


    check(part2(testInput) == 3121910778619L)
    prcp(part2(input))
}
