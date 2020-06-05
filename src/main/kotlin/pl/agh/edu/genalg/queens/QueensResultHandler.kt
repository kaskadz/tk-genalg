package pl.agh.edu.genalg.queens

import pl.agh.edu.genalg.framework.flow.ResultHandler
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation
import pl.agh.edu.genalg.onemax.OneMaxHyperparameters

class QueensResultHandler(hyperparameters: QueensHyperparameters, reporter: Reporter) :
    ResultHandler<Queens, EvaluatedQueens, QueensHyperparameters>(hyperparameters, reporter) {

    override fun selectResults(results: EvaluatedPopulation<Queens, EvaluatedQueens>): Collection<EvaluatedQueens> {
        return results.evaluatedEntities
    }
}