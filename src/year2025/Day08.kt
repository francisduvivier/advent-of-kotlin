package year2025

import checkEquals
import prcp
import readInput

fun main() {

    fun part1(input: List<String>, amount: Int): Long {
        val junctions = input.map { jk2jt(it) }.toSet()
        assert(junctions.size == input.size)
        val biggestCircuits: List<Int> = addClosest(junctions, amount)
        return biggestCircuits.map { it.toLong() }.reduce { a, b -> a * b }
    }

    fun part2(input: List<String>): Int {
        val junctions = input.map { jk2jt(it) }.toSet()
        assert(junctions.size == input.size)
        val distanceSet =
            junctions.flatMap { j1 -> junctions.filter { it != j1 }.map { getConnectionPair(j1, it) } }.toSet()
        val closest = distanceSet.sortedByDescending { it.second }.map { it.first }.toMutableList()
        val circuitMap = mutableMapOf<Triple<Int, Int, Int>, MutableSet<Triple<Int, Int, Int>>>()
        junctions.forEach { junction -> circuitMap[junction] = mutableSetOf(junction)}
        
        while (true) {
            val closePair = closest.removeLast()
            val circuit = circuitMap[closePair.first]!!
            circuitMap[closePair.second]!!.forEach { circuitMap[it] = circuit; circuit.add(it) }
            if(circuitMap.values.toSet().size == 1){
                return closePair.first.first * closePair.second.first
            }
        }
     
        throw Error("fail")
    }

    // test if implementation meets criteria from the description, like:
    val day = 8
    println("Starting Day${day}")
    val testInput = readInput("Day$day.test")
    checkEquals(part1(testInput, 10), 40)
    val input = readInput("Day$day")
    prcp(part1(input, 1000))
    checkEquals(part2(testInput), 25272)
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
): Pair<Pair<Triple<Int, Int, Int>, Triple<Int, Int, Int>>, Long> {
    val sortedJunctions = arrayOf(first, second).sortedBy { jk2jt(it) }
    return Pair(Pair(sortedJunctions[0], sortedJunctions[1]), jDistance(first, second))
}

fun jDistance(first: Triple<Int, Int, Int>, second: Triple<Int, Int, Int>): Long {
    return first.toList().mapIndexed { index: Int, c: Int -> c - second.toList()[index] }.map { it.toLong() }.sumOf { it * it }
}

fun addClosest(
    junctions: Set<Triple<Int, Int, Int>>,
    amount: Int
): List<Int> {
    val distanceSet =
        junctions.flatMap { j1 -> junctions.filter { it != j1 }.map { getConnectionPair(j1, it) } }.toSet()
    val closest = distanceSet.sortedBy { it.second }.map { it.first }
    val circuitMap = mutableMapOf<Triple<Int, Int, Int>, MutableSet<Triple<Int, Int, Int>>>()
    junctions.forEach { junction -> circuitMap[junction] = mutableSetOf(junction)}
    for (closePair in closest.slice(0..<amount)) {
        val circuit = circuitMap[closePair.first]!!
        circuitMap[closePair.second]!!.forEach { circuitMap[it] = circuit; circuit.add(it) }
    }
    val circuitSet = circuitMap.values.toSet()
    val allSizes = circuitSet.map { it.size }.sortedDescending().toList()
    return allSizes.slice(0..<3)
}
