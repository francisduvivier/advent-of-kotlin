fun main() {
    fun part1(input: List<String>): Long {
        val allInvalids = mutableListOf<Long>()
        for(line in input) {
            val (a,b) = line.split("-").map { it.toLong() }
            val invalids: List<Long> = findInvalidsInRange(a, b)
            allInvalids.addAll(invalids)
        }
        return allInvalids.sum()
    }

    fun part2(input: List<String>): Long {
        val allInvalids = mutableListOf<Long>()
        for(line in input) {
            val (a,b) = line.split("-").map { it.toLong() }
            val invalids: List<Long> = findInvalidsInRange2(a, b)
            allInvalids.addAll(invalids)
        }
        return allInvalids.sum()    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02.test")
    check(part1(testInput) == 1227775554L)
    val input = readInput("Day02")
    prcp(part1(input))


    check(part2(testInput) == 4174379265L)
    prcp(part2(input))
}

fun findInvalidsInRange(a: Long, b: Long): List<Long> {
    return findInvalidsInRangeNaive(a, b)
}

fun findInvalidsInRangeNaive(a: Long, b: Long): List<Long> {
    val numbers = a..b
    return numbers.filter { isInvalid(it) }
}

fun findInvalidsInRange2(a: Long, b: Long): List<Long> {
    val numbers = a..b
    return numbers.filter { isInvalid2(it) }
}

fun isInvalid2(it: Long): Boolean {
    return it.toString().matches(Regex("^(.*)\\1+$"))
}
fun isInvalid(it: Long): Boolean {
    return it.toString().matches(Regex("^(.*)\\1$"))
}
