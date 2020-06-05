package pl.agh.edu.genalg.queens

import pl.agh.edu.genalg.framework.flow.PopulationSelector
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation
import kotlin.math.roundToInt

class QueensPopulationSelector(hyperparameters: QueensHyperparameters, reporter: Reporter) :
    PopulationSelector<Queens, EvaluatedQueens, QueensHyperparameters>(hyperparameters, reporter) {

    override fun selectPopulation(evaluatedPopulation: EvaluatedPopulation<Queens, EvaluatedQueens>): EvaluatedPopulation<Queens, EvaluatedQueens> {
        val numberOfEntitiesToDie = evaluatedPopulation.size * hyperparameters.deathRate

        val survivedEntities = evaluatedPopulation.evaluatedEntities
            .sortedBy { it.fitness }
            .take((evaluatedPopulation.size - numberOfEntitiesToDie).roundToInt())

        return EvaluatedPopulation(survivedEntities)
    }
}