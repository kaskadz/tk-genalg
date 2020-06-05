package pl.agh.edu.genalg.queens

import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity
import pl.agh.edu.genalg.framework.model.Hyperparameters

const val BoardSize = 8
const val QueensCount = 8

fun <T> Iterable<T>.takeRandom(count: Int): Iterable<T> {
    return this.shuffled().take(count)
}

data class Position(val rowNum: Int, val colNum: Int) {
    companion object Factory {
        fun getRandom(count: Int): List<Position> {
            return (0 until (BoardSize * BoardSize))
                .takeRandom(count)
                .map {
                    Position(it / BoardSize, it % BoardSize)
                }
        }

        fun getRandom(): Position {
            return (0 until (BoardSize * BoardSize))
                .random()
                .let {
                    Position(it / BoardSize, it % BoardSize)
                }
        }
    }
}

class Queens(val positions: Set<Position>) : Entity {

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
            for (row in 0 until BoardSize) {
                for (col in 0 until BoardSize) {
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
    val tournamentSize: Int
) : Hyperparameters(maxIterationsCount, initialPopulationSize, mutationRate)
