package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.model.*

abstract class PopulationEvaluator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(val hyperparameters: H) {

    fun evaluatePopulation(population: Population<E>): EvaluatedPopulation<E, F> {
        val evaluatedEntities = population.entities.map { e ->
            evaluateEntity(e)
        }.toList()

        return EvaluatedPopulation(evaluatedEntities)
    }

    abstract fun evaluateEntity(entity: E): F
}