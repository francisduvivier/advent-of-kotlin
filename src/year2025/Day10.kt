package year2025

import checkEquals
import prcp
import readInput

typealias State = List<Int>
typealias ButtonContributionVector = List<Int>

data class Flags(val DISABLE_EXTRA_CONSTRAINTS: Boolean = true) {
}
val flags = Flags()

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
            val buttonSettings: List<List<Int>> =
                matchGroups[2]!!.value.split(" ")
                    .map { comp -> comp.slice(1..<comp.lastIndex).split(",").map { it.toInt() } }
            val originalWantedNumbers: State =
                matchGroups[3]!!.value.split(",").map { it.toInt() }

            val maxPossibleCost = originalWantedNumbers.sum().toLong()
            val minCostMap = mutableMapOf<State, Int>()
            val originalButtonContributionVectors: List<ButtonContributionVector> =
                buttonSettings.map { bs -> originalWantedNumbers.mapIndexed { index, curr -> if (bs.contains(index)) 1 else 0 } }
            val (wantedNumbers, buttonContributionVectors) = addConstraints(
                originalWantedNumbers,
                originalButtonContributionVectors
            )
            val state: State = wantedNumbers.map { 0 }

            fun strictlyBetterCostFound(state: State, cost: Int): Boolean =
                minCostMap.getOrDefault(state, Int.MAX_VALUE) <= cost ||
                        minCostMap.any {
                            val minCostState = it.key
                            val betterFound = cost >= it.value &&
                                    (0..minCostState.lastIndex).all { state[it] <= minCostState[it] }
                            betterFound
                        }

            // Plan: we want to add equations, so that we can prune much faster, for this, we need to change from boolean to numbers and we should allow smaller than 0
            // For this, we need to convert our components to vectors and we need to then add extra constraints.
            // Extra constraints means that 
            //  - our wanted state will possibly have negative values
            //  - when we apply a component, we need to apply it also to the other rows.
            // Then how can we think about these other rows? these other rows are extra counters which have a different button wiring
            // So if we already rewrite our code to work with vectors and possibly negative counters, then, we could already try it out without needing to already do the row reduction.
            // So currently, our components are indexes of counters.
            // Instead, our components need to become multipliers for counters, so it's still a list of ints, but they need to be interpreted differently, so lets give it a different name
            // But so not to forget is that the columns are for the buttons, and the rows are for the counters
            // so extra counter means an extra setting for every button
            fun checkSolutionsRec(
                state: State,
                pushesDone: Int,
            ): Int? {
                var index = 0
                if (state.any { it > wantedNumbers[index++] }) {
                    return null
                }
                if (strictlyBetterCostFound(state, pushesDone)) {
                    return null
                }
                minCostMap[state] = pushesDone
                if (state == wantedNumbers) {
                    return pushesDone
                }
                if (pushesDone > maxPossibleCost) {
                    assert(false)
                }
                val results = buttonContributionVectors.mapNotNull {
                    val newState = pushButton(it, state)
                    checkSolutionsRec(
                        newState,
                        pushesDone + 1
                    )
                }
                return results.minOrNull()
            }

            print("---- Solve line $line")
            val startTime = System.nanoTime()
            val checkSolutionsRec = checkSolutionsRec(state, 0)
            val timeDiffMs = (System.nanoTime() - startTime).toDouble() / 1_000_000
            println(", SOL: $checkSolutionsRec in $timeDiffMs ms")
            return checkSolutionsRec!!
        }

        return input.map { solveLine(it) }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val day = 10
    println("Starting Day${day}")
    println("Flags: $flags")
    val testInput = readInput("Day$day.test")
    checkEquals(part1(testInput), 7)
    val input = readInput("Day$day")
    prcp(part1(input))
    checkEquals(part2(testInput), 33)
    println("Flags: $flags")
    prcp(part2(input))
}


fun addConstraints(
    wantedState: State,
    originalButtonContributionVectors: List<ButtonContributionVector>
): Pair<State, List<ButtonContributionVector>> {
    if (flags.DISABLE_EXTRA_CONSTRAINTS) return Pair(wantedState, originalButtonContributionVectors)
    val augmented = toAugmentedMatrix(wantedState, originalButtonContributionVectors)
    val augmentedWithExtraConstraints = augmented.toMutableList()

    val extraRows = augmented.mapIndexed { index, ints ->
        augmented.slice((index + 1)..augmented.lastIndex)
            .map { other -> if (other.last() < ints.last()) ints.doMin(other) else other.doMin(ints) }
    }.flatten()
    augmentedWithExtraConstraints.addAll(extraRows)
    val newPair = fromAugmented(augmentedWithExtraConstraints)
    return newPair
}

private fun fromAugmented(
    augmented: List<List<Int>>
): Pair<State, List<ButtonContributionVector>> {
    val contributionVector = (0..<augmented.nbCols() - 1).map { augmented.getCol(it) }
    val pair = Pair(augmented.getCol(augmented[0].lastIndex), contributionVector)
    return pair
}

private fun List<List<Int>>.nbCols(): Int {
    return this[0].size
}

private fun List<Int>.doMin(other: List<Int>): List<Int> {
    return this.mapIndexed { index, it -> it - other[index] }
}

private fun toAugmentedMatrix(
    originalState: State,
    originalButtonContributionVectors: List<ButtonContributionVector>
): List<List<Int>> = originalState.mapIndexed { index, s ->
    originalButtonContributionVectors.getCol(index) + listOf(s)
}

private fun List<ButtonContributionVector>.getCol(index: Int): List<Int> {
    return this.map { row -> row[index] }
}


fun pushComp(comp: List<Int>, state: List<Int>): List<Int> {
    val newState = state.toMutableList()
    for (i in comp) {
        newState[i] = (newState[i] + 1) % 2
    }
    return newState
}

fun pushButton(vector: ButtonContributionVector, state: List<Int>): List<Int> {
    val newState = state.toMutableList()
    for (i in 0 until vector.size) {
        newState[i] = newState[i] + vector[i]
    }
    return newState
}