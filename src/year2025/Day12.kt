package year2025

import BoolMatrix
import CharMatrix
import checkEquals
import prcp
import readInput
import toMatrix

fun parseShapes(input: List<String>): List<BoolMatrix> {
    val inputParts = input.joinToString("\n").split(Regex("""\n\n\d+x\d+:"""))

    val boxes = inputParts[0].slice("0:\n".length..inputParts[0].lastIndex).split(Regex("""\n\n\d:\n"""))
    return boxes.map { toMatrix(it.split("\n")).toBoolMatrix() }
}

fun parseRegions(input: List<String>): List<RegionConfig> {
    val joinedInput = input.joinToString("\n")
    val inputParts = joinedInput.split(Regex("""\n\n\d+x\d+:"""))
    val regionLines = joinedInput.slice(inputParts[0].length + 2..joinedInput.lastIndex).split(Regex("""\n"""))
    return regionLines.map { parseRegionLine(it) }
}
typealias MatrixSize = Pair<Int, Int>
typealias RegionConfig = Pair<MatrixSize, List<Int>>

fun parseRegionLine(it: String): RegionConfig {
    val parts = it.split(": ")
    val dimensions = parts[0].split("x").map { it.toInt() }
    val matrixSize: MatrixSize = Pair(dimensions[0], dimensions[1])
    val amounts = parts[1].split(" ").map { it.toInt() }
    return Pair(matrixSize, amounts)
}

fun generateAllMasks(it: BoolMatrix, region: Mask): Set<Mask> {
    return it.generateRotations().flatMap { it.generateFlips() }.flatMap { it.generateTranslations(region) }.toSet()
}
typealias Mask = BoolMatrix

private fun BoolMatrix.generateTranslations(region: Mask): Set<Mask> {
    val rowOptions = region.rows() - this.rows()
    val colOptions = region.cols() - this.cols()
    val noChange: MatrixSize = Pair(region.rows(), region.cols()) // TODO TODO TODO

    return (0..rowOptions).flatMap { rowOffset ->
        (0..colOptions).map { colOffset ->
            val translatedMask = noChange.toMask().mapIndexed { rowIndex, row ->
                val translatedRow = rowIndex - rowOffset
                if (rowIndex < rowOffset || translatedRow < 0 || translatedRow >= this.rows()) row else row.mapIndexed { colIndex, value ->
                    val translatedCol = colIndex - colOffset
                    if (colIndex < colOffset || translatedCol < 0 || translatedCol >= this.cols()) value else {
                        this[translatedRow][translatedCol]
                    }
                }
            }
            region.addMask(translatedMask)!!
        }
    }.toSet()
}

private fun BoolMatrix.generateFlips(): List<BoolMatrix> {
    return listOf(this, this.flippedHorizontal())
}

private fun BoolMatrix.flippedHorizontal(): BoolMatrix {
    return this.map { it.reversed() }
}

private fun BoolMatrix.flippedVertical(): BoolMatrix {
    return this.reversed().map { it }
}

private fun BoolMatrix.generateRotations(): List<BoolMatrix> {
    val rotated180 = this.flippedHorizontal().flippedVertical()
    return listOf(this, this.rotate90(), rotated180, rotated180.rotate90())
}

private fun BoolMatrix.rotate90(): BoolMatrix {
    return this.reversed() // TODO TODO TODO
}

fun BoolMatrix.rows(): Int {
    return this.size
}

fun CharMatrix.toBoolMatrix(trueChar: Char = '#'): BoolMatrix {
    return this.map { row -> row.map { char -> if (char == trueChar) true else false } }
}

fun BoolMatrix.cols(): Int {
    return this[0].size
}

fun List<Int>.decreaseIndex(indexToDecrease: Int): List<Int> {
    return this.mapIndexed { index, amount -> if (indexToDecrease == index) amount - 1 else amount }
}

fun main() {

    fun part1(input: List<String>): Long {
        val shapes: List<BoolMatrix> = parseShapes(input)
        val regions: List<RegionConfig> = parseRegions(input)

        fun canFit(regionConfig: RegionConfig): Boolean {
            val startMask = regionConfig.first.toMask()
            val shapeMaskOptions: List<Set<Mask>> = shapes.map { generateAllMasks(it, startMask) }

            fun findOptionRec(amounts: List<Int>, accumulatedMask: BoolMatrix): Boolean {
                if (amounts.all { it == 0 }) return true
                for (todoIndex in amounts.indices) {
                    if (amounts[todoIndex] == 0) continue
                    return shapeMaskOptions[todoIndex].any {
                        val newMask = accumulatedMask.addMask(it) ?: return@any false
                        return@any findOptionRec(
                            amounts.decreaseIndex(todoIndex),
                            newMask
                        )
                    }
                }
                assertNever()
            }

            return findOptionRec(regionConfig.second, startMask)
        }

        return regions.count {
            println("doing region" + it);
            val result = canFit(it)
            println("result: " + result);
            return@count result
        }.toLong()
    }

    fun part2(input: List<String>): Long {
        return 0
    }

    check(listOf(1, 2).decreaseIndex(0) == listOf(0, 2))
    check(listOf(1, 2).decreaseIndex(1) == listOf(1, 1))
    check(toMatrix(listOf("..", "##")).toBoolMatrix().addMask(toMatrix(listOf(".#", "##")).toBoolMatrix()) == null)
    check(
        toMatrix(listOf("..", "##")).toBoolMatrix().addMask(toMatrix(listOf(".#", "..")).toBoolMatrix()) == toMatrix(
            listOf(".#", "##")
        ).toBoolMatrix()
    )
    val day = 12
    println("Starting Day${day}")
    val testInput = readInput("Day$day.test")
    checkEquals(part1(testInput), 2)
    val input = readInput("Day$day")
    prcp(part1(input))
    checkEquals(part2(testInput), 0)
    prcp(part2(input))
}

private fun MatrixSize.toMask(): Mask {
    return List(
        this.first,
        {
            List(
                this.second,
                { false })
        })
}

private fun assertNever(): Nothing {
    throw Error("can never come here")
}

private fun BoolMatrix.addMask(mask: BoolMatrix): BoolMatrix? {
    return this.mapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, currVal ->
            val maskVal = mask[rowIndex][colIndex]
            if (maskVal && currVal) return null
            else return@mapIndexed maskVal || currVal
        }
    }
}
