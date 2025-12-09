import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min


enum class Rotation(val xMult: Int, val yMult: Int, val zMult: Int = 1) {
    R1(1, 1), R2(1, -1), R3(-1, 1), R4(-1, -1),
    R5(1, 1, -1), R6(1, -1, -1), R7(-1, 1, -1), R8(-1, -1, -1);

    fun mutatePos(pos: Pos3D): Pos3D {
        return Pos3D(pos.x * xMult, pos.y * yMult, pos.z * zMult)
    }
}

enum class Facing(val mutator: (Pos3D) -> Pos3D) {
    F1({ Pos3D(it.x, it.y, it.z) }),
    F2({ Pos3D(it.x, it.z, it.y) }),
    F3({ Pos3D(it.z, it.x, it.y) }),
    F4({ Pos3D(it.z, it.y, it.x) }),
    F5({ Pos3D(it.y, it.x, it.z) }),
    F6({ Pos3D(it.y, it.z, it.x) });

    fun mutatePos(pos: Pos3D): Pos3D {
        return mutator(pos)
    }
}

class Orientation(val direction: Facing, val rotation: Rotation) {
    fun mutatePos(pos3D: Pos3D): Pos3D {
        return rotation.mutatePos(direction.mutatePos(pos3D))
    }
}

class Pos3D(val x: Int, val y: Int, val z: Int) {
    constructor(list: List<Int>) : this(list[0], list[1], list[2])

    val all = listOf(x, y, z)
    override fun toString(): String {
        return all.joinToString(",")
    }

    fun diff(other: Pos3D): Pos3D {
        return Pos3D(all.mapIndexed { index, it -> it - other.all[index] })
    }

    fun orient(orientation: Orientation): Pos3D {
        return orientation.mutatePos(this);
    }

    fun add(other: Pos3D): Pos3D {
        return Pos3D(all.mapIndexed { index, it -> it + other.all[index] })
    }

    override fun hashCode(): Int {
        return this.toString().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        try {
            val otherPos = other as Pos3D
            return otherPos.diff(this).all.all { it == 0 }
        } catch (e: Error) {
            return false
        }
    }

    fun manHattan(): Int {
        return all.map { it.absoluteValue }.sum()
    }
}



data class Pos(val x: Long, val y: Long) {
    val pair = Pair(this.x, this.y)
    constructor(pos: Pair<Long, Long>): this(pos.first, pos.second)

    // Inclusive
    fun isBetween(other1: Pos, other2: Pos): Boolean {
        return sameX(listOf(other1, other2)) && this.yIsBetween(other1, other2) || sameY(listOf(other1, other2)) && this.xIsBetween(other1, other2)
    }

    fun sameX(others: List<Pos>): Boolean = others.all { it.x == this.x }
    fun sameY(others: List<Pos>): Boolean = others.all { it.y == this.y }

    fun xIsBetween(other1: Pos, other2: Pos): Boolean {
        return numberIsBetween(other1.x, other2.x, x)
    }

    fun yIsBetween(other1: Pos, other2: Pos): Boolean {
        return numberIsBetween(other1.y, other2.y, y)
    }
}

private fun numberIsBetween(first: Long, second: Long, maybeBetween: Long): Boolean =
    (min(first, second)..max(first, second)).contains(maybeBetween)