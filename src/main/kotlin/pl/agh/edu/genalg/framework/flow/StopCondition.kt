package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation
import pl.agh.edu.genalg.framework.model.Hyperparameters

abstract class StopCondition<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    val hyperparameters: H,
    val reporter: Reporter
) {
    abstract fun shouldStop(iterationCount: Int, evaluatedPopulation: EvaluatedPopulation<E, F>): Boolean
}