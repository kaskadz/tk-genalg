package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.model.*

abstract class SinglePopulationRecombinator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(hyperparameters: H) :
    PopulationRecombinator<E, F, H>(hyperparameters) {

    override fun recombineSelectedPopulation(evaluatedPopulation: EvaluatedPopulation<E, F>): Population<E> {
        val newEntities = evaluatedPopulation.evaluatedEntities
            .map { it.entity }
            .map { recombineSingle(it) }

        return Population(newEntities)
    }

    protected abstract fun recombineSingle(entity: E): E
}