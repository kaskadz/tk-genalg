package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.model.*

abstract class PopulationRecombinator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(val hyperparameters: H) {
    abstract fun selectPopulationToBeRecombined(evaluatedPopulation: EvaluatedPopulation<E, F>): EvaluatedPopulation<E, F>
    abstract fun recombinePopulation(evaluatedPopulation: EvaluatedPopulation<E, F>): Population<E>
}