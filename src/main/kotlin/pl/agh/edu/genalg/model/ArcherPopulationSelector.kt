package pl.agh.edu.genalg.model

import pl.agh.edu.genalg.framework.EvaluatedPopulation
import pl.agh.edu.genalg.framework.PopulationSelector
import kotlin.math.abs

class ArcherPopulationSelector(hyperparameters: ArcherHyperparameters) :
    PopulationSelector<Archer, EvaluatedArcher, ArcherHyperparameters>(hyperparameters) {

    override fun selectPopulation(evaluatedPopulation: EvaluatedPopulation<Archer, EvaluatedArcher>): EvaluatedPopulation<Archer, EvaluatedArcher> {
        val survived = evaluatedPopulation.evaluatedEntities
            .filter { abs(it.error) < hyperparameters.maxAbsError }
        return EvaluatedPopulation(survived)
    }
}