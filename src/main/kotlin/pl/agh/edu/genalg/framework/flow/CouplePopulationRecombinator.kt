package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.model.*

abstract class CouplePopulationRecombinator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(hyperparameters: H) :
    PopulationRecombinator<E, F, H>(hyperparameters) {

    override fun recombineSelectedPopulation(evaluatedPopulation: EvaluatedPopulation<E, F>): Population<E> {
        val newEntities = evaluatedPopulation.evaluatedEntities
            .map { it.entity }
            .shuffled()
            .withIndex()
            .partition { it.index % 2 == 0 }
            .run { first.zip(second) }
            .map { recombineCouple(it.first.value, it.second.value) }

        return Population(newEntities)
    }

    protected abstract fun recombineCouple(entity1: E, entity2: E): E
}