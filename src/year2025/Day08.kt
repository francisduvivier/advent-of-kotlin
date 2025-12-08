package year2025

import checkEquals
import prcp
import readInput
import toMatrix
import kotlin.math.absoluteValue

fun main() {

    fun part1(input: List<String>, amount: Int): Long {
        val junctions = input.map { jk2jt(it) }.toSet()
        assert(junctions.size == input.size)
        val biggestCircuits: List<Int> = addClosest(junctions, amount)
        return biggestCircuits.map { it.toLong() }.reduce { a, b -> a * b }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val day = 8
    println("Starting Day${day}")
    val testInput = readInput("Day$day.test")
    checkEquals(part1(testInput, 10), 40)
    val input = readInput("Day$day")
    prcp(part1(input, 1000))
    checkEquals(part2(testInput), 0)
    prcp(part2(input))
}


fun jk2jt(junctionKey: String): Triple<Int, Int, Int> {
    val split = junctionKey.split(",").map { it.toInt() }
    return Triple(split[0], split[1], split[2])
}

fun jk2jt(junctionTriple: Triple<Int, Int, Int>): String {
    return junctionTriple.toList().joinToString(",")
}

fun getConnectionPair(
    first: Triple<Int, Int, Int>,
    second: Triple<Int, Int, Int>
): Pair<Pair<Triple<Int, Int, Int>, Triple<Int, Int, Int>>, Int> {
    val sortedJunctions = arrayOf(first, second).sortedBy { jk2jt(it) }
    return Pair(Pair(sortedJunctions[0], sortedJunctions[1]), jDistance(first, second))
}

fun jDistance(first: Triple<Int, Int, Int>, second: Triple<Int, Int, Int>): Int {
    return first.toList().mapIndexed { index: Int, c: Int -> c - second.toList()[index] }.sumOf { it * it }
}

fun addClosest(
    junctions: Set<Triple<Int, Int, Int>>,
    amount: Int
): List<Int> {
    val distanceSet =
        junctions.flatMap { j1 -> junctions.filter { it != j1 }.map { getConnectionPair(j1, it) } }.toSet()
    val closest = distanceSet.sortedBy { it.second }.map { it.first }.toSet()
    val connections = mutableMapOf<Triple<Int, Int, Int>, MutableSet<Triple<Int, Int, Int>>>()
    var amountAdded = 0
    val circuitMap = mutableMapOf<Triple<Int, Int, Int>, MutableSet<Triple<Int, Int, Int>>>()
    for (closePair in closest) {
        if (circuitMap[closePair.first] != null && circuitMap[closePair.first] == circuitMap[closePair.second]) {
            continue
        }
        addConnection(connections, closePair)
        addConnection(connections, Pair(closePair.second, closePair.first))

        val connectedOthers = connections[closePair.first]
        var circuit = connections[closePair.first]!!.firstNotNullOfOrNull { circuitMap[it] }
        if (circuit == null) {
            circuit = connections[closePair.second]!!.firstNotNullOfOrNull { circuitMap[it] }
        }
        if (circuit == null) {
            circuit = mutableSetOf()
        }
        val connectedOthers2 = connections[closePair.first]
        val all =
            connectedOthers2!!.toList() + listOf(closePair.second) + connectedOthers!!.toList() + listOf(closePair.first)
        all.forEach { circuitMap[it] = circuit; circuit.add(it) }
        if (++amountAdded >= amount) {
            break
        }
    }
    val circuitSet = circuitMap.values.toSet()
    val allSizes = circuitSet.map { it.size }.sortedDescending().toList()
    return allSizes.slice(0..<3)
}

fun addConnection(
    connections: MutableMap<Triple<Int, Int, Int>, MutableSet<Triple<Int, Int, Int>>>,
    it: Pair<Triple<Int, Int, Int>, Triple<Int, Int, Int>>
) {
    if (!connections.containsKey(it.first)) {
        connections[it.first] = mutableSetOf()
    }
    connections[it.first]!!.add(it.second)
}
