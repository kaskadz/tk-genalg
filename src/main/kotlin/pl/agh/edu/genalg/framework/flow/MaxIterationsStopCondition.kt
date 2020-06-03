package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation
import pl.agh.edu.genalg.framework.model.Hyperparameters

class MaxIterationsStopCondition<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    hyperparameters: H,
    reporter: Reporter
) : StopCondition<E, F, H>(hyperparameters, reporter) {

    override fun shouldStop(
        iterationCount: Int,
        evaluatedPopulation: EvaluatedPopulation<E, F>
    ): Boolean {
        return iterationCount >= hyperparameters.maxIterationsCount
    }
}