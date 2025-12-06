fun main() {
    fun part1(input: List<String>): Long {
        val (freshRanges, ingredients) = parseInput(input)
        return ingredients.count { isFresh(it, freshRanges) }.toLong()
    }

    fun part2(input: List<String>): Long {
        check(countNewRanges(1L..3, listOf(1L..3)) == 0L)
        check(countNewRanges(1L..3, listOf(1L..2)) == 1L)
        check(countNewRanges(1L..3, listOf(2L..3)) == 1L)
        check(countNewRanges(1L..2, listOf(2L..3)) == 1L)
        check(countNewRanges(3L..4, listOf(2L..3)) == 1L)
        check(countNewRanges(3L..3, listOf(2L..3)) == 0L)
        check(countNewRanges(3L..3, listOf(2L..2)) == 1L)

        val (freshRanges, _) = parseInput(input)
        val doneRanges = mutableListOf<LongRange>()

        return freshRanges.sumOf {
            val added = countNewRanges(it, doneRanges)
            if (added > 0) {
                doneRanges.add(it)

            }
            added
        }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05.test")
    check(part1(testInput) == 3L)
    val input = readInput("Day05")
    prcp(part1(input))


    check(part2(testInput) == 14L)
    check(part2(testInput + testInput) == 14L)
    prcp(part2(input))
}

fun countNewRanges(it: LongRange, doneRanges: List<LongRange>): Long {
    var start = it.start
    var endInclusive = it.endInclusive
    for (doneRange in doneRanges) {
        if (doneRange.contains(start)) {
            start = (doneRange.endInclusive + 1).coerceAtLeast(start)
        }
        if (doneRange.contains(endInclusive)) {
            endInclusive = (doneRange.start - 1).coerceAtMost(endInclusive)
        }
        if (endInclusive < start) {
            return 0L
        }
    }
    var alreadyCountedWithin = 0L
    val finalRange = start..endInclusive
    for (doneRange in doneRanges) {
        if (finalRange.contains(doneRange.start)) {
            alreadyCountedWithin += countSize(doneRange)
        }
    }
    return countSize(finalRange) - alreadyCountedWithin
}

fun countSize(range: LongRange): Long {
   return range.endExclusive - range.start
}


fun isFresh(ingredient: Long, freshRanges: List<LongRange>): Boolean {
    return freshRanges.any { it.contains(ingredient) }
}

fun parseInput(input: List<String>): Pair<List<LongRange>, List<Long>> {
    val ranges = input.filter { it.contains("-") }
        .map {
            val rangeParts = it.split("-").map { it.toLong() }
            check(rangeParts[0] <= rangeParts[1])
            LongRange(rangeParts[0], rangeParts[1])
        }
    val ingredients = input.filter { it.matches(Regex("""^\d+$""")) }.map { it.toLong() }
    return Pair(ranges, ingredients)
}
