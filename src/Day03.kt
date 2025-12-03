import util.prcp
import util.readInput

fun getBestJoltage(input: String): Long {
    val numbersChars = input.split("").slice(1..input.length)
    val max1 = numbersChars.slice(0..<numbersChars.size - 1).maxBy { it.toInt() }
    val max2 = numbersChars.slice((numbersChars.indexOf(max1))+1..<numbersChars.size).maxBy { it.toInt() }
    println(max1 + max2)
    return (max1 + max2).toLong()
}

fun main() {
    fun part1(input: List<String>): Long {
        return input.map { getBestJoltage(it) }.sumOf { it }
    }

    fun part2(input: List<String>): Long {
        TODO("todo")
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03.test")
    check(part1(testInput) == 357L)
    val input = readInput("Day03")
    prcp(part1(input))


    check(part2(testInput) == 4174379265L)
    prcp(part2(input))
}
