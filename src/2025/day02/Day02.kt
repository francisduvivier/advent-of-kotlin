import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.io.File
import java.lang.Exception
import java.math.BigInteger
import java.security.MessageDigest


fun main() {
    fun part1(input: List<String>): Int {
        val allInvalids = mutableListOf<Int>()
        for(line in input) {
            val (a,b) = line.split("-").map { it.toInt() }
            val invalids: List<Int> = findInvalidsInRange(a, b)
            allInvalids.addAll(invalids)
        }
        return allInvalids.sum()
    }

    fun part2(input: List<String>): Int {
        TODO("Not yet implemented")
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02.test")
    check(part1(testInput) == 1227775554)
    val input = readInput("Day02")
    prcp(part1(input))


//    check(part2(testInput) == 900)
//    prcp(part2(input))
}

fun findInvalidsInRange(a: Int, b: Int): List<Int> {
    return findInvalidsInRangeNaive(a, b)
}

fun findInvalidsInRangeNaive(a: Int, b: Int): List<Int> {
    val numbers = a..b
    return numbers.filter { isInvalid(it) }
}

fun isInvalid(it: Int): Boolean {
    return it.toString().matches(Regex("^(.*)\\1$"))
}
