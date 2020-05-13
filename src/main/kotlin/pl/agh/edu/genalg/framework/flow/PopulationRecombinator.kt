package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.model.*

abstract class PopulationRecombinator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(val hyperparameters: H) {
    protected abstract fun selectPopulationToBeRecombined(evaluatedPopulation: EvaluatedPopulation<E, F>): EvaluatedPopulation<E, F>
    protected abstract fun recombineSelectedPopulation(evaluatedPopulation: EvaluatedPopulation<E, F>): Population<E>

    fun recombinePopulation(evaluatedPopulation: EvaluatedPopulation<E, F>): Population<E> {
        val selectedPopulationToBeRecombined = selectPopulationToBeRecombined(evaluatedPopulation)
        val newOffspring = recombineSelectedPopulation(selectedPopulationToBeRecombined)
        val newPopulation = (evaluatedPopulation.evaluatedEntities.map { it.entity } + newOffspring.entities)
        return Population(newPopulation)
    }
}