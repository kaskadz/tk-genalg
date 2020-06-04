package pl.agh.edu.genalg.onemax

import pl.agh.edu.genalg.framework.flow.ResultHandler
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation

class OneMaxResultHandler(
    hyperparameters: OneMaxHyperparameters,
    reporter: Reporter
) :
    ResultHandler<BinaryVector, EvaluatedBinaryVector, OneMaxHyperparameters>(hyperparameters, reporter) {

    override fun selectResults(results: EvaluatedPopulation<BinaryVector, EvaluatedBinaryVector>): Collection<EvaluatedBinaryVector> {
        return results.evaluatedEntities
            .sortedByDescending { it.numberOfOnes }
            .take(1)
            .toList()
    }
}