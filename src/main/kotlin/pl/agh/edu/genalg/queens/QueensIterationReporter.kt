package pl.agh.edu.genalg.queens

import pl.agh.edu.genalg.framework.flow.IterationReporter
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation

class QueensIterationReporter(hyperparameters: QueensHyperparameters, reporter: Reporter) :
    IterationReporter<Queens, EvaluatedQueens, QueensHyperparameters>(hyperparameters, reporter) {

    override suspend fun report(
        iterationCount: Int,
        evaluatedPopulation: EvaluatedPopulation<Queens, EvaluatedQueens>
    ) {
        val results = evaluatedPopulation.evaluatedEntities.map { it.fitness }
        val best = results.min()
        val mean = results.average()

        reporter.metric("best", best)
        reporter.metric("mean", mean)

        if (iterationCount % 100 == 0) {
            reporter.log("Best=$best; Mean=$mean")
        }
    }
}