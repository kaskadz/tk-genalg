package pl.agh.edu.genalg.framework

class MaxIterationsStopCondition<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(hyperparameters: H) :
    StopCondition<E, F, H>(hyperparameters) {

    override fun shouldStop(
        iterationCount: Int,
        evaluatedPopulation: EvaluatedPopulation<E, F>
    ): Boolean {
        return iterationCount >= hyperparameters.maxIterationsCount
    }
}