package pl.agh.edu.genalg.framework

abstract class PopulationMutator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(hyperparameters: H) {
    abstract fun selectPopulationToBeRecombined(evaluatedPopulation: EvaluatedPopulation<E, F>): EvaluatedPopulation<E, F>
    abstract fun mutateEntity(entity: E): E
}