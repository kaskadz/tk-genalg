package pl.agh.edu.genalg.queens

import pl.agh.edu.genalg.framework.flow.StopCondition
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation

class QueensStopCondition(hyperparameters: QueensHyperparameters, reporter: Reporter) :
    StopCondition<Queens, EvaluatedQueens, QueensHyperparameters>(hyperparameters, reporter) {

    override fun shouldStop(
        iterationCount: Int,
        evaluatedPopulation: EvaluatedPopulation<Queens, EvaluatedQueens>
    ): Boolean {
        return iterationCount >= hyperparameters.maxIterationsCount
                || evaluatedPopulation.size <= hyperparameters.minimalPopulationSize
                || evaluatedPopulation.evaluatedEntities.any { it.fitness == 0 }
    }

    override fun shouldAllStop(
        iterationCount: Int,
        evaluatedPopulation: EvaluatedPopulation<Queens, EvaluatedQueens>
    ): Boolean {
        return evaluatedPopulation.evaluatedEntities.any { it.fitness == 0 }
    }
}