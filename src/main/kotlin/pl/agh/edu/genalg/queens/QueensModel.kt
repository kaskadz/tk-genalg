package pl.agh.edu.genalg.queens

import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity
import pl.agh.edu.genalg.framework.model.Hyperparameters

fun <T> Iterable<T>.takeRandom(count: Int): Iterable<T> {
    return this.shuffled().take(count)
}

data class Position(val rowNum: Int, val colNum: Int) {
    companion object Factory {
        fun getRandom(boardSize: Int, count: Int): List<Position> {
            return (0 until (boardSize * boardSize))
                .takeRandom(count)
                .map {
                    Position(it / boardSize, it % boardSize)
                }
        }

        fun getRandom(boardSize: Int): Position {
            return (0 until (boardSize * boardSize))
                .random()
                .let {
                    Position(it / boardSize, it % boardSize)
                }
        }
    }
}

class Queens(val positions: Set<Position>, private val boardSize: Int) : Entity {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Queens

        if (positions != other.positions) return false

        return true
    }

    override fun hashCode(): Int {
        return positions.hashCode()
    }

    override fun toString(): String {
        return buildString {
            for (row in 0 until boardSize) {
                for (col in 0 until boardSize) {
                    val char = if (positions.contains(Position(row, col))) 'Q' else '.'
                    append(char)
                }
                this.appendln()
            }
        }
    }

}

class EvaluatedQueens(entity: Queens, val fitness: Int) : EvaluatedEntity<Queens>(entity)

class QueensHyperparameters(
    maxIterationsCount: Int,
    initialPopulationSize: Int,
    mutationRate: Double,
    val deathRate: Double,
    val reproductionRate: Double,
    val migrationRate: Double,
    val iterationsCountBetweenMigrations: Int,
    val minimalPopulationSize: Int,
    val maxNumberOfQueensToMutate: Int,
    val tournamentSize: Int,
    val boardSize: Int
) : Hyperparameters(maxIterationsCount, initialPopulationSize, mutationRate)
