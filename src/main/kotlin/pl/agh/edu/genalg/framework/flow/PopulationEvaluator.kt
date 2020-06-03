package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.*

abstract class PopulationEvaluator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    val hyperparameters: H,
    val reporter: Reporter
) {

    fun evaluatePopulation(population: Population<E>): EvaluatedPopulation<E, F> {
        val evaluatedEntities = population.entities.map { e ->
            evaluateEntity(e)
        }

        return EvaluatedPopulation(evaluatedEntities)
    }

    protected abstract fun evaluateEntity(entity: E): F
}