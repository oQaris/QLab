package io.deeplay.qlab.algorithm

import io.deeplay.qlab.parser.models.Unit
import io.deeplay.qlab.parser.models.input.EnemyLocation

class TestUtils {

    fun genSetUnit(count: Int) = buildSet {
        repeat(count) {
            add(Unit(it.toString(), it.toDouble()))
        }
    }

    fun genSetLoc(
        count: Int,
        levels: List<Int> = listOf(1, 5, 10, 1000),
        maxPositions: List<Int> = listOf(6, 9)
    ) = buildSet {
        repeat(count) {
            val maxPos = maxPositions.random()
            add(
                EnemyLocation(
                    it.toString(),
                    levels.random(),
                    maxPos,
                    genSetUnit(
                        (0 until maxPos).random()
                    ).toMutableList()
                )
            )
        }
    }

    // костыли для джавы
    fun genSetLocs(count: Int) = genSetLoc(count)
}
