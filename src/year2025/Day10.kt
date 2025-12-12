package year2025

import checkEquals
import min
import prcp
import readInput

typealias State = List<Int>


fun main() {
    fun part1(input: List<String>): Long {
        fun solveLine(line: String): Long {
            // line example: [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
            val matchGroups = Regex("""\[([.#]+)] (.*) \{.*""").matchEntire(line)?.groups!!
            val wantedChars = matchGroups[1]!!.value.toCharArray().map { if (it == '#') 1 else 0 }
            println(wantedChars)
            val components: List<List<Int>> =
                matchGroups[2]!!.value.split(" ")
                    .map { comp -> comp.slice(1..<comp.lastIndex).split(',').map { it.toInt() } }

            val state: List<Int> = wantedChars.map { 0 }
            val nbCoeffs = components.size

            fun checkSolutionsRec(
                components: List<List<Int>>,
                state: List<Int>,
                trySize: Int,
            ): Boolean {
                if (state == wantedChars) {
                    return true
                }
                if (trySize == 0) {
                    return false
                }
                for (comp in components) {
                    val newState = pushComp(comp, state)
                    val checkSolutionsRec = checkSolutionsRec(
                        components.slice(1 until components.size),
                        newState,
                        trySize - 1
                    )
                    if (checkSolutionsRec) {
                        return true
                    }
                }
                return false
            }

            for (trySize in 1..nbCoeffs) {
                if (checkSolutionsRec(components, state, trySize)) {
                    return trySize.toLong()
                }
            }
            throw Exception("No solution found")
        }

        // min sum(X) where X *(dot product) S = WANTED
        // [1,0,1] = x1 * (0,1,1)  + x2 * (1,1,0) * x3 * (0,1,0) + x4 (1,0,0) => x1=1, x2=1 =>X = (1,1,0,0)
        return input.map { solveLine(it) }.sum()
    }

    fun part2(input: List<String>): Long {
        fun solveLine(line: String): Long {
            // line example: [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
            val matchGroups = Regex("""\[([.#]+)] (.*) \{(.*)}""").matchEntire(line)?.groups!!
            val components: List<List<Int>> =
                matchGroups[2]!!.value.split(" ")
                    .map { comp -> comp.slice(1..<comp.lastIndex).split(",").map { it.toInt() } }
            val wantedNumbers: State =
                matchGroups[3]!!.value.split(",").map { it.toInt() }

            val state: State = wantedNumbers.map { 0 }
            val maxPossibleCost = wantedNumbers.sum().toLong()
            val minCostMap = mutableMapOf<State, Long>()

            fun checkSolutionsRec(
                components: List<List<Int>>,
                state: State,
                pushesDone: Long,
            ): Long? {
                var index = 0
                val badState = state.find { it > wantedNumbers[index++] }
                if (badState !== null) {
                    return null
                }
                if (minCostMap[state] != null && minCostMap[state]!! < pushesDone) {
                    return null
                }
                minCostMap[state] = pushesDone 
                if (state == wantedNumbers) {
                    return pushesDone
                }
                if (pushesDone > maxPossibleCost) {
                    assert(false)
                }
                val results = components.mapNotNull {
                    val newState = pushComp2(it, state)
                    checkSolutionsRec(
                        components,
                        newState,
                        pushesDone + 1
                    )
                }
                return results.minOrNull()
            }

            println("---- Solve line $line")
            return checkSolutionsRec(components.sortedBy { it.size }, state, 0)!!
        }

        return input.map { solveLine(it) }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val day = 10
    println("Starting Day${day}")
    val testInput = readInput("Day$day.test")
    checkEquals(part1(testInput), 7)
    val input = readInput("Day$day")
    prcp(part1(input))
    checkEquals(part2(testInput), 33)
    prcp(part2(input))
}


fun pushComp(comp: List<Int>, state: List<Int>): List<Int> {
    val newState = state.toMutableList()
    for (i in comp) {
        newState[i] = (newState[i] + 1) % 2
    }
    return newState
}

fun pushComp2(comp: List<Int>, state: List<Int>): List<Int> {
    val newState = state.toMutableList()
    for (i in comp) {
        newState[i] = newState[i] + 1
    }
    return newState
}