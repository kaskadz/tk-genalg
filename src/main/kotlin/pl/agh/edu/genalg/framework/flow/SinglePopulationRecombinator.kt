package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.model.*

abstract class SinglePopulationRecombinator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(hyperparameters: H) :
    PopulationRecombinator<E, F, H>(hyperparameters) {

    override fun recombineSelectedPopulation(population: Population<E>): Population<E> {
        val newEntities = population.entities
            .map { recombineSingle(it) }

        return Population(newEntities)
    }

    protected abstract fun recombineSingle(entity: E): E
}