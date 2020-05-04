package pl.agh.edu.genalg.framework

abstract class PopulationSelector<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(val hyperparameters: H) {
    abstract fun selectPopulation(evaluatedPopulation: EvaluatedPopulation<E, F>): EvaluatedPopulation<E, F>
}