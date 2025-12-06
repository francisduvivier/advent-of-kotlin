package year2025

import checkEquals
import prcp

import java.io.File

fun readInputUnTrimmed(name: String): List<String> = File("input", "$name.txt").readLines()


fun main() {
    fun part1(input: List<String>): Long {
        val matrixPart = input.slice(0..<input.lastIndex)
        val operators = input[input.lastIndex].trim().split(Regex("\\s+"))

        val matrix = matrixPart.map { it.trim().split(Regex("\\s+")).map { it.toLong() } }
        return operators.mapIndexed { col: Int, op ->
            val values = matrix.map { it[col] }
            if (op == "+") {
                val sum = values.sum()
                return@mapIndexed sum
            } else {
                val multiplied = getMultiplied(values)
                return@mapIndexed multiplied
            }
        }.sum()
    }

    fun part2(input: List<String>): Long {
        val matrixPart = input.slice(0..<input.lastIndex)
        val operators = input[input.lastIndex].trim().split(Regex("\\s+"))
        val operatorSpaces = input[input.lastIndex].slice(1..input[input.lastIndex].lastIndex).split(Regex("[*+]+"))
        var totalIndex = 0
        return operators.mapIndexed { col: Int, op ->
            val columnSize = operatorSpaces[col].length
            val valuesWithSpaces = matrixPart.map { it.slice(totalIndex..totalIndex + columnSize) }
            totalIndex += columnSize + 1
            val numbers =
                (0..columnSize).map { subCol ->
                    val subColValue = valuesWithSpaces.map { it[subCol] }.joinToString("").trim()
                    subColValue
                }.filter { it.isNotBlank() }.map { it.toLong() }
            if (op == "+") {
                val sum = numbers.sum()
                return@mapIndexed sum
            } else {
                val multiplied = getMultiplied(numbers)
                return@mapIndexed multiplied
            }
        }.sum()
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInputUnTrimmed("Day06.test")
    checkEquals(part1(testInput), 4277556L)
    val input = readInputUnTrimmed("Day06")
    prcp(part1(input))


    checkEquals(part2(testInput), 3263827L)
    prcp(part2(input))
}

private fun getMultiplied(numbers: List<Long>): Long = numbers.reduce { acc, next -> acc * next }
