package io.deeplay.qlab.data

fun <T> splitRounds(rounds: List<T>, testFraq: Double, shuffle: Boolean = false): Pair<List<T>, List<T>> {
    require(testFraq in 0.0..1.0) { "Некорректный testFraq" }

    val roundsCopy = if (shuffle) rounds.shuffled() else rounds

    val testSize = (roundsCopy.size * testFraq).toInt()
    val trainRounds = roundsCopy.take(rounds.size - testSize)
    val testRounds = roundsCopy.takeLast(testSize)

    return trainRounds to testRounds
}
