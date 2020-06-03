package pl.agh.edu.genalg.onemax

import pl.agh.edu.genalg.framework.Reporter
import pl.agh.edu.genalg.framework.flow.StopCondition
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation

class OneMaxStopCondition(hyperparameters: OneMaxHyperparameters, reporter: Reporter) :
    StopCondition<BinaryVector, EvaluatedBinaryVector, OneMaxHyperparameters>(hyperparameters, reporter) {

    override fun shouldStop(
        iterationCount: Int,
        evaluatedPopulation: EvaluatedPopulation<BinaryVector, EvaluatedBinaryVector>
    ): Boolean {
        return iterationCount >= hyperparameters.maxIterationsCount
                || evaluatedPopulation.evaluatedEntities.size <= hyperparameters.minimalPopulationSize
                || evaluatedPopulation.evaluatedEntities.any { it.numberOfOnes == hyperparameters.vectorSize }
    }
}