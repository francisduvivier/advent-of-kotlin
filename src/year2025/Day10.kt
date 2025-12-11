package year2025

import checkEquals
import prcp
import readInput

fun main() {
    fun part1(input: List<String>): Long {
        // min sum(X) where X *(dot product) S = WANTED
        // [1,0,1] = x1 * (0,1,1)  + x2 * (1,1,0) * x3 * (0,1,0) + x4 (1,0,0) => x1=1, x2=1 =>X = (1,1,0,0)
        return input.map { solveLine(it) }.sum()
    }

    fun part2(input: List<String>): Long {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val day = 10
    println("Starting Day${day}")
    val testInput = readInput("Day$day.test")
    checkEquals(part1(testInput), 7)
    val input = readInput("Day$day")
    prcp(part1(input))
    checkEquals(part2(testInput), 0)
    prcp(part2(input))
}

fun solveLine(line: String): Long {
    // line example: [.##.] (3) (1,3) (2) (2,3) (0,2) (0,1) {3,5,4,7}
    val matchGroups = Regex("""\[([.#]+)] (.*) \{.*""").matchEntire(line)?.groups!!
    val wantedChars = matchGroups[1]!!.value.toCharArray().map { if (it == '#') 1 else 0 }
    println(wantedChars)
    val components: List<List<Int>> =
        matchGroups[2]!!.value.split(" ").map { comp -> comp.slice(1..<comp.lastIndex).split(',').map { it.toInt() } }

    val state: List<Int> = wantedChars.map { 0 }
    val nbCoeffs = components.size
    for (trySize in 1..nbCoeffs) {
        if (checkSolutionsRec(components, state, wantedChars, trySize, 0)) {
            return trySize.toLong()
        }
    }
    throw Exception("No solution found")
}

fun checkSolutionsRec(
    components: List<List<Int>>,
    state: List<Int>,
    wantedChars: List<Int>,
    trySize: Int,
    stateOffset: Int
): Boolean {
    if (state == wantedChars) {
        return true
    }
    if (trySize == 0) {
        return false
    }
    if (state[0] == wantedChars[0]) {
        return checkSolutionsRec(
            components,
            state.slice(1..state.lastIndex),
            wantedChars.slice(1..wantedChars.lastIndex),
            trySize,
            stateOffset + 1
        )
    }
    for (comp in components) {
        val newState = pushComp(comp, state, stateOffset)
        if (newState[0] != wantedChars[0]) {
            return false
        }
        val checkSolutionsRec = checkSolutionsRec(
            components,
            newState.slice(1..wantedChars.lastIndex),
            wantedChars.slice(1..wantedChars.lastIndex),
            trySize - 1,
            stateOffset + 1
        )
        if (checkSolutionsRec) {
            return true
        }
    }
    return false
}

fun pushComp(comp: List<Int>, state: List<Int>, stateOffset: Int): List<Int> {
    val newState = state.toMutableList()
    for (i in comp.filter { it >= stateOffset }) {
        val relativeIndex = i - stateOffset
        newState[relativeIndex] = (newState[relativeIndex] + 1) % 2
    }
    return newState
}
