package year2021

import prcp
import readInput2021
import checkEquals

fun main() {
    fun part1(input: List<String>): Int {
        var count = 0;
        for (i in 1..input.size - 1) {
            if (input[i].toInt() > input[i - 1].toInt()) {
                count++
            }
        }
        return count;
    }

    fun part2(input: List<String>): Int {
        var count = 0;
        for (i in 3..input.size - 1) {
            if (input[i].toInt() + input[i - 1].toInt() + input[i - 2].toInt() > input[i - 1].toInt() + input[i - 2].toInt() + input[i - 3].toInt()) {
                count++
            }
        }
        return count;
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput2021("Day01.test")
    checkEquals(part1(testInput), 7)

    val input = readInput2021("Day01")
    prcp(part1(input))
    prcp(part2(input))
}
