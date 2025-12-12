package year2025

import checkEquals
import prcp
import readInput

fun main() {
    fun getConnections(input: List<String>): Map<String, Set<String>> {
        val mutableMapOf = mutableMapOf<String, Set<String>>()
        for (line in input) {
            //aaa: you hhh
            val parts = line.split(": ")
            mutableMapOf[parts[0]] = parts[1].split(" ").toSet()
        }
        return mutableMapOf
    }

    fun part1(input: List<String>): Long {

        val connections: Map<String, Set<String>> = getConnections(input)
        fun getAllPathsRec(value: String): Long {
            if (value == "out") {
                return 1
            }
            return connections[value]!!.sumOf { getAllPathsRec(it) }
        }

        return getAllPathsRec("you")
    }

    fun part2(input: List<String>): Long {
        val connections: Map<String, Set<String>> = getConnections(input)
        val done = mutableMapOf<String, MutableMap<String, Long>>()
        fun conditionKey(history: Set<String>): String {
            val joined = listOf("dac", "fft").map { history.contains(it) }.joinToString(",")
            return joined
        }

        val OK_KEY = conditionKey(setOf("dac", "fft"))
        for (i in listOf("dac", "")) {
            for (j in listOf("fft", "")) {
                done[conditionKey(setOf(i, j))] = mutableMapOf()
            }
        }
        fun getAllPathsRec(history: Set<String>, value: String): Long {
            val cKey = conditionKey(history)
            if (done.containsKey(cKey) && done[cKey]!!.containsKey(value)) {
                return done[cKey]!![value]!!
            }
            if (history.contains(value)) TODO("LOOPING")
            if (value == "out") {
                if (conditionKey(history) == OK_KEY) return 1
                else return 0
            }
            val result = connections[value]!!.sumOf { getAllPathsRec(history + setOf(value), it) }
            done[cKey]!![value] = result
            return result
        }

        return getAllPathsRec(setOf(), "svr")
    }

    // test if implementation meets criteria from the description, like:
    val day = 11
    println("Starting Day${day}")
    val testInput = readInput("Day$day.test")
    checkEquals(part1(testInput), 5)
    val input = readInput("Day$day")
    prcp(part1(input))
    val testInput2 = readInput("Day$day.2.test")
    checkEquals(part2(testInput2), 2)
    prcp(part2(input))
}