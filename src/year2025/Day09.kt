package year2025

import Pos
import checkEquals
import prcp
import readInput
import kotlin.math.abs
import kotlin.rem
import kotlin.text.get

fun surface(first: Pair<Long, Long>, second: Pair<Long, Long>): Long {
    return first.toList().mapIndexed { index: Int, c: Long -> abs(c - second.toList()[index]) + 1L }
        .reduce { acc, lng -> acc * lng }
}

fun isValid(square: Pair<Pos, Pos>, lines: List<Pair<Pos, Pos>>): Boolean {
    val squarePointsToCheck = listOf(Pos(square.first.x, square.second.y), Pos(square.second.x, square.first.y))
    return squarePointsToCheck.all { isInside(it, lines) }
}

fun isInside(pointToCheck: Pos, lines: List<Pair<Pos, Pos>>): Boolean {
    val exactLineMatch = lines.any { pointToCheck.isBetween(it.first, it.second) }
    if (exactLineMatch) {
        return true
    }
    val amountOfLinesCrossedRight = lines
        .count { crossesRight(pointToCheck, it) }
    return amountOfLinesCrossedRight % 2 == 1
}

fun crossesRight(pointToCheck: Pos, boundaryLine: Pair<Pos, Pos>): Boolean {
    val lineX = boundaryLine.first.x
    return pointToCheck.yIsBetweenExcl(boundaryLine.first, boundaryLine.second) && pointToCheck.x < lineX
}

fun main() {
    fun part1(input: List<String>): Long {
        val pairs = input.map { it.split(",").map { it.toLong() } }.map { Pair(it[0], it[1]) }
        val posPairs = pairs.flatMap { first ->
            pairs.filter { first !== it }.map { second -> Pair(Pair(Pos(first), Pos(second)), surface(second, first)) }
        }
        val sortedPairs = posPairs.sortedByDescending { it.second }
        return sortedPairs.first().second
    }

    fun part2(input: List<String>): Long {
        val pairs = input.map { it.split(",").map { it.toLong() } }.map { Pair(it[0], it[1]) }
        val posPairs = pairs.flatMap { first ->
            pairs.filter { first !== it }.map { second -> Pair(Pair(Pos(first), Pos(second)), surface(second, first)) }
        }
        val sortedPairs = posPairs.sortedByDescending { it.second }
        val others: List<Pos> = pairs.map { Pos(it) }
        val lines: List<Pair<Pos, Pos>> =
            others.mapIndexed { index, pos -> Pair(pos, others[(index + 1) % others.size]) }
        val badLine = lines.find {
            lines.filter { l -> l != it }.any { other ->
                val badOther = other.first.sameY(it.toList() + listOf(other.second)) && (other.first.xIsBetween(
                    it.first,
                    it.second
                ) || other.second.xIsBetween(it.first, it.second))
                badOther
            }
        }
        if (badLine != null) {
            throw Exception("There should not be any overlapping lines")
        }
        val solution = sortedPairs.find {
            isValid(it.first, lines)
        }
        return solution!!.second
    }


    // test if implementation meets criteria from the description, like:
    val day = 9
    println("Starting Day${day}")
    val testInput = readInput("Day$day.test")
    checkEquals(part1(testInput), 50)
    val input = readInput("Day$day")
    prcp(part1(input))
    checkEquals(part2(testInput), 24)
    prcp(part2(input))
}
