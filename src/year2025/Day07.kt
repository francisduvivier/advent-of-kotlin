package year2025

import checkEquals
import prcp
import readInput

fun main() {
    fun part1(input: List<String>): Int {
        var splits = 0
        input.reduce { acc, next ->
            var beamState = acc.replace('S', '|').toCharArray()
            next.forEachIndexed { index, nextCh ->
                if (nextCh == '^' && beamState[index] == '|') {
                    splits++
                    beamState[index] = '.'
                    if (index > 0) beamState[index - 1] = '|'
                    if (index < beamState.lastIndex) beamState[index + 1] = '|'
                }

            }
            beamState.joinToString("")
        }
        return splits
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    // test if implementation meets criteria from the description, like:
    val day = 7
    println("Starting Day${day}")
    val testInput = readInput("Day$day.test")
    checkEquals(part1(testInput), 21)
    val input = readInput("Day$day")
    prcp(part1(input))
    checkEquals(part2(testInput), 0)
    prcp(part2(input))
}
