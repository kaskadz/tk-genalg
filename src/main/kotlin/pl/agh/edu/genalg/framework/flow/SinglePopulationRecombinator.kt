package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.*

abstract class SinglePopulationRecombinator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    hyperparameters: H,
    reporter: Reporter
) : PopulationRecombinator<E, F, H>(hyperparameters, reporter) {

    override fun recombineSelectedPopulation(population: Population<E>): Population<E> {
        val newEntities = population.entities
            .flatMap { recombineSingle(it) }

        return Population(newEntities)
    }

    protected abstract fun recombineSingle(entity: E): Collection<E>
}