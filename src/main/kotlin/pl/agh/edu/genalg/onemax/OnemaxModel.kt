package pl.agh.edu.genalg.onemax

import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity
import pl.agh.edu.genalg.framework.model.Hyperparameters


class BinaryVector(val genes: IntArray) : Entity

class EvaluatedBinaryVector(entity: BinaryVector, val numberOfOnes: Int) : EvaluatedEntity<BinaryVector>(entity) {
    override fun toString(): String {
        return "EvaluatedBinaryVector(numberOfOnes=$numberOfOnes)"
    }
}

class OneMaxHyperparameters(
    maxIterationsCount: Int,
    initialPopulationSize: Int,
    val vectorSize: Int,
    val deathRate: Double,
    val reproductionRate: Double,
    val mutationRate: Double,
    val mutationMaxScope: Double,
    val migrationRate: Double,
    val iterationsCountBetweenMigrations: Int,
    val minimalPopulationSize: Int
) : Hyperparameters(maxIterationsCount, initialPopulationSize)