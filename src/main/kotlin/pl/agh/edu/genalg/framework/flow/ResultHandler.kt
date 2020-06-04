package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation
import pl.agh.edu.genalg.framework.model.Hyperparameters
import pl.agh.edu.genalg.onemax.OneMaxHyperparameters

abstract class ResultHandler<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    val hyperparameters: OneMaxHyperparameters,
    val reporter: Reporter
) {
    abstract fun selectResults(results: EvaluatedPopulation<E, F>): Collection<F>
}