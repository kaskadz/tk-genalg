package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.model.*

abstract class PopulationMutator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(val hyperparameters: H) {
    protected abstract fun mutateEntity(entity: E): E

    fun mutatePopulation(
        population: Population<E>
    ): Population<E> {
        val mutatedPopulation = population.entities.map { mutateEntity(it) }
        return Population(mutatedPopulation)
    }
}