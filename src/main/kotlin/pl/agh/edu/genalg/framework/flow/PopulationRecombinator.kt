package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.*

abstract class PopulationRecombinator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    val hyperparameters: H,
    val reporter: Reporter
) {
    protected abstract fun selectPopulationToBeRecombined(evaluatedPopulation: EvaluatedPopulation<E, F>): Population<E>
    protected abstract fun recombineSelectedPopulation(population: Population<E>): Population<E>

    fun recombinePopulation(evaluatedPopulation: EvaluatedPopulation<E, F>): SemiEvaluatedPopulation<E, F> {
        val selectedPopulationToBeRecombined = selectPopulationToBeRecombined(evaluatedPopulation)
        val newOffspring = recombineSelectedPopulation(selectedPopulationToBeRecombined)
        return SemiEvaluatedPopulation(newOffspring.entities, evaluatedPopulation.evaluatedEntities)
    }
}