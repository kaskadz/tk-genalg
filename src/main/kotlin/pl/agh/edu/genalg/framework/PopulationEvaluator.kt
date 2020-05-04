package pl.agh.edu.genalg.framework

abstract class PopulationEvaluator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(val hyperparameters: H) {
    abstract fun evaluatePopulation(population: Population<E>): Collection<F>
    abstract fun sortPopulation(evaluatedEntities: Collection<F>): EvaluatedPopulation<E, F>
}