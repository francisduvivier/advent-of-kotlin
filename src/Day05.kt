import util.prcp
import util.readInput


fun main() {
    fun part1(input: List<String>): Long {
        val (freshRanges, ingredients) = parseInput(input)
        return ingredients.count { isFresh(it.toULong(), freshRanges) }.toLong()
    }

    fun part2(input: List<String>): ULong {
        check(getRemainingRange(1UL..3UL, listOf(1UL..3UL)) == null)
        check(getRemainingRange(1UL..3UL, listOf(1UL..2UL))?.count() == 1)
        check(getRemainingRange(1UL..3UL, listOf(2UL..3UL))?.count() == 1)
        check(getRemainingRange(1UL..2UL, listOf(2UL..3UL))?.count() == 1)
        check(getRemainingRange(3UL..4UL, listOf(2UL..3UL))?.count() == 1)
        check(getRemainingRange(3UL..3UL, listOf(2UL..3UL)) == null)
        check(getRemainingRange(3UL..3UL, listOf(2UL..2UL))?.count() == 1)
        check(getRemainingRange(3UL..3UL, listOf(4UL..5UL))?.count() == 1)

        val (freshRanges, _) = parseInput(input)
        val doneRanges = mutableListOf<ULongRange>()
        var sum = 0UL
        freshRanges.forEach {

            val added = getRemainingRange(it, doneRanges)
            if (added != null) {
                doneRanges.add(added)
                sum += added.endExclusive - added.first
            }
        }

        return sum
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05.test")
    check(part1(testInput) == 3L)
    val input = readInput("Day05")
    prcp(part1(input))


    check(part2(testInput) == 14UL)
    prcp(part2(input).toLong())
}

fun getRemainingRange(it: ULongRange, doneRanges: List<ULongRange>): ULongRange? {
    var start = it.start
    var endInclusive = it.endInclusive
    for (doneRange in doneRanges) {
        if (doneRange.contains(start)) {
            start = (doneRange.endInclusive + 1UL).coerceAtLeast(start)
        }
        if (doneRange.contains(endInclusive)) {
            endInclusive = (doneRange.start - 1UL).coerceAtMost(endInclusive)
        }
        if (endInclusive < start) {
            return null
        }
    }
    return start..endInclusive
}


fun isFresh(ingredient: ULong, freshRanges: List<ULongRange>): Boolean {
    return freshRanges.any { it.contains(ingredient) }
}

fun parseInput(input: List<String>): Pair<List<ULongRange>, List<Long>> {
    val ranges = input.filter { it.contains("-") }
        .map {
            val rangeParts = it.split("-").map { it.toULong() }
            check(rangeParts[0] <= rangeParts[1])
            ULongRange(rangeParts[0], rangeParts[1])
        }
    val ingredients = input.filter { it.matches(Regex("""^\d+$""")) }.map { it.toLong() }
    return Pair(ranges, ingredients)
}
