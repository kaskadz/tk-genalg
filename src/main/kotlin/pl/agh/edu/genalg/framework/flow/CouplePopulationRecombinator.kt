package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.Reporter
import pl.agh.edu.genalg.framework.model.*

abstract class CouplePopulationRecombinator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    hyperparameters: H,
    reporter: Reporter
) : PopulationRecombinator<E, F, H>(hyperparameters, reporter) {

    override fun recombineSelectedPopulation(population: Population<E>): Population<E> {
        val newEntities = population.entities
            .shuffled()
            .withIndex()
            .partition { it.index % 2 == 0 }
            .run { first.zip(second) }
            .map { recombineCouple(it.first.value, it.second.value) }

        return Population(newEntities)
    }

    protected abstract fun recombineCouple(entity1: E, entity2: E): E
}