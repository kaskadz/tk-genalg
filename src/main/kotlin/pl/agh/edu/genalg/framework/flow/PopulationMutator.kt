package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.model.*

abstract class PopulationMutator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(hyperparameters: H) {
    abstract fun selectPopulationToBeMutated(
        evaluatedPopulation: EvaluatedPopulation<E, F>,
        newOffspring: Population<E>
    ): EvaluatedPopulation<E, F>

    abstract fun mutateEntity(entity: E): E
}