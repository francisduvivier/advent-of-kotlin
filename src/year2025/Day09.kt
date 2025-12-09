package year2025

import checkEquals
import prcp
import readInput
import kotlin.math.abs

fun surface(first: Pair<Long, Long>, second: Pair<Long, Long>): Long {
    return first.toList().mapIndexed { index: Int, c: Long -> abs(c - second.toList()[index]) + 1L }
        .reduce { acc, lng -> acc * lng }
}

fun main() {
    fun part1(input: List<String>): Long {
        val pairs = input.map { it.split(",").map { it.toLong() } }.map { Pair(it[0], it[1]) }
        val surfaces = pairs.flatMap { first ->
            pairs.filter { first !== it }.map { second -> surface(second, first) }
        }
        return surfaces.max()
    }

    fun part2(input: List<String>): Long {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val day = 9
    println("Starting Day${day}")
    val testInput = readInput("Day$day.test")
    checkEquals(part1(testInput), 50)
    val input = readInput("Day$day")
    prcp(part1(input))
    checkEquals(part2(testInput), 0)
    prcp(part2(input))
}
