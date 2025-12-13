package year2025

import checkEquals
import prcp
import readInput

typealias State = List<Int>


fun main() {
    fun part1(input: List<String>): Int {
        fun solveLine(line: String): Int {
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
                    return trySize
                }
            }
            throw Exception("No solution found")
        }

        // min sum(X) where X *(dot product) S = WANTED
        // [1,0,1] = x1 * (0,1,1)  + x2 * (1,1,0) * x3 * (0,1,0) + x4 (1,0,0) => x1=1, x2=1 =>X = (1,1,0,0)
        return input.map { solveLine(it) }.sum()
    }

    fun part2(input: List<String>): Int {
        fun solveLine(line: String): Int {
            // line example: [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
            val matchGroups = Regex("""\[([.#]+)] (.*) \{(.*)}""").matchEntire(line)?.groups!!
            val components: List<List<Int>> =
                matchGroups[2]!!.value.split(" ")
                    .map { comp -> comp.slice(1..<comp.lastIndex).split(",").map { it.toInt() } }
            val wantedNumbers: State =
                matchGroups[3]!!.value.split(",").map { it.toInt() }

            val state: State = wantedNumbers.map { 0 }
            val maxPossibleCost = wantedNumbers.sum().toLong()
            val minCostMap = mutableMapOf<State, Int>()
            val skippedOptions = sortedSetOf<Pair<State, Int>>(
                Comparator { p1, p2 -> p1.second - p2.second }
            )

            fun isOkState(tryState: State, pushCount: Int): Boolean {
                if (minCostMap[tryState] != null && minCostMap[tryState]!! < pushCount) return false
                var index1 = 0
                return !tryState.any { it > wantedNumbers[index1++] }
            }

            fun checkSolutionsRec(
                components: List<List<Int>>,
                state: State,
                pushCount: Int,
            ): Int? {

                if (state == wantedNumbers) {
                    return pushCount
                }
                if (pushCount > maxPossibleCost) {
                    assert(false)
                }
                val results = components.mapNotNull {
                    var totalPushCount = pushCount
                    var newState: State = state
                    do {
                        val tryState = pushComp2(it, newState)
                        val newPushCount = totalPushCount + 1
                        if (isOkState(tryState, newPushCount)) {
                            if (newState !== state) {
                                skippedOptions.add(Pair(newState, totalPushCount))
                            }
                            minCostMap[tryState] = newPushCount
                            totalPushCount = newPushCount
                            newState = tryState
                        } else {
                            break
                        }
                    } while (true)
                    if (totalPushCount == pushCount) {
                        null
                    } else {
                        checkSolutionsRec(
                            components,
                            newState,
                            totalPushCount
                        )
                    }
                }
                return results.minOrNull()
            }

            println("---- Solve line $line")
            var tryState = state
            var pushCount = 0
            do {
                val solution = checkSolutionsRec(components.sortedBy { it.size }, tryState, pushCount)
                if (solution != null) {
                    return solution
                }
                val newOption = skippedOptions.removeFirst()
                tryState = newOption.first
                pushCount = newOption.second
            } while (true)

        }

        return input.map {
            try {
                solveLine(it)
            } catch (e: Throwable) {
                println("Error: ${e.message}")
                0
            }
        }.sum()
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

fun pushComp2(comp: List<Int>, state: State): List<Int> {
    val newState = state.toMutableList()
    for (i in comp) {
        newState[i] = newState[i] + 1
    }
    return newState
}