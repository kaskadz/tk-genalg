package pl.agh.edu.genalg.onemax

import pl.agh.edu.genalg.framework.flow.IterationReporter
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation

class OneMaxIterationReporter(hyperparameters: OneMaxHyperparameters, reporter: Reporter) :
    IterationReporter<BinaryVector, EvaluatedBinaryVector, OneMaxHyperparameters>(hyperparameters, reporter) {
    override suspend fun report(
        iterationCount: Int,
        evaluatedPopulation: EvaluatedPopulation<BinaryVector, EvaluatedBinaryVector>
    ) {
        val results = evaluatedPopulation.evaluatedEntities.map { it.numberOfOnes }
        val best = results.max()
        val mean = results.average()

        reporter.metric("best", best)
        reporter.metric("mean", mean)

        if (iterationCount % 100 == 0) {
            reporter.log("Best=$best; Mean=$mean")
        }
    }
}