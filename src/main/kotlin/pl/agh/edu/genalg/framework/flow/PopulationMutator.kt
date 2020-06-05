package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.*
import kotlin.random.Random

abstract class PopulationMutator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    val hyperparameters: H,
    val reporter: Reporter
) {
    protected abstract fun mutateEntity(entity: E): E

    fun mutatePopulation(
        population: Population<E>
    ): Population<E> {
        val mutatedPopulation = population.entities.map {
            if (Random.nextDouble() < hyperparameters.mutationRate) mutateEntity(it) else it
        }
        return Population(mutatedPopulation)
    }
}