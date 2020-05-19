package pl.agh.edu.genalg.onemax

import pl.agh.edu.genalg.framework.flow.PopulationSelector
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation
import kotlin.math.roundToInt

class OneMaxPopulationSelector(hyperparameters: OneMaxHyperparameters) :
    PopulationSelector<BinaryVector, EvaluatedBinaryVector, OneMaxHyperparameters>(hyperparameters) {

    override fun selectPopulation(evaluatedPopulation: EvaluatedPopulation<BinaryVector, EvaluatedBinaryVector>): EvaluatedPopulation<BinaryVector, EvaluatedBinaryVector> {
        val numberOfEntitiesToDie = evaluatedPopulation.evaluatedEntities.size * hyperparameters.deathRate

        val survivedEntities = evaluatedPopulation.evaluatedEntities
            .sortedByDescending { it.numberOfOnes }
            .take((evaluatedPopulation.evaluatedEntities.size - numberOfEntitiesToDie).roundToInt())

        return EvaluatedPopulation(survivedEntities)
    }
}