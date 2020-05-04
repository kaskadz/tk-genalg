package pl.agh.edu.genalg.framework

abstract class StopCondition<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(val hyperparameters: H) {
    abstract fun shouldStop(iterationCount: Int, evaluatedPopulation: EvaluatedPopulation<E, F>): Boolean
}