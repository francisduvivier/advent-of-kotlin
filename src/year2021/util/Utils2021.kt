import java.io.File


/**
 * Reads lines from the given input txt file.
 */
fun readInput2021(name: String): List<String> {
    val day = Regex("\\d+").find(name)!!.value
    val paddedDay = day.padStart(2, '0')
    return File("input/2021", "${name.replace(day, paddedDay)}.txt").readLines().map { it.trim() }
}