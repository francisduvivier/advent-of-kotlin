fun main() {
    fun part1(input: List<String>): Long {
        val matrix = toCharMatrix(input)

        val paperRolls = rowCols(matrix).filter { matrix[it.first][it.second] == '@' }
        return paperRolls.count {
            val nbs: List<Pair<Int, Int>> = get8NeighborLocations(matrix, it.first, it.second)
            nbs.count { matrix[it.first][it.second] == '@' } < 4
        }.toLong()

    }

    fun part2(input: List<String>): Long {
        val matrix = toCharMatrix(input)
        var totalRemoved = 0L
        var removed = 0
        do {
            val paperRolls = rowCols(matrix).filter { matrix[it.first][it.second] == '@' }
            val removable = paperRolls.filter {
                val nbs: List<Pair<Int, Int>> = get8NeighborLocations(matrix, it.first, it.second)
                nbs.count { matrix[it.first][it.second] == '@' } < 4
            }
            removed = removable.size
            totalRemoved += removable.size
            removable.forEach { removable -> matrix[removable.first][removable.second] = '.' }
        } while (removed > 0)
        return totalRemoved
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04.test")
    check(part1(testInput) == 13L)
    val input = readInput("Day04")
    prcp(part1(input))


    check(part2(testInput) == 43L)
    prcp(part2(input))
}
