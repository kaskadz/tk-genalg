package pl.agh.edu.genalg.framework

abstract class PopulationRecombinator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(hyperparameters: H) {
    abstract fun selectPopulationToBeRecombined(evaluatedPopulation: EvaluatedPopulation<E, F>): EvaluatedPopulation<E, F>
    abstract fun recombinePopulation(evaluatedPopulation: EvaluatedPopulation<E, F>): Population<E>
}