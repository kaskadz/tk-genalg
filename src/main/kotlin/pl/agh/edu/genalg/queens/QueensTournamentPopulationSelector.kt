package pl.agh.edu.genalg.queens

import pl.agh.edu.genalg.framework.flow.PopulationSelector
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation

class QueensTournamentPopulationSelector(hyperparameters: QueensHyperparameters, reporter: Reporter) :
    PopulationSelector<Queens, EvaluatedQueens, QueensHyperparameters>(hyperparameters, reporter) {

    override fun selectPopulation(evaluatedPopulation: EvaluatedPopulation<Queens, EvaluatedQueens>): EvaluatedPopulation<Queens, EvaluatedQueens> {
        val numberOfEntitiesToLive = evaluatedPopulation.size * (1 - hyperparameters.deathRate)

        val entitiesPool = evaluatedPopulation.evaluatedEntities.toMutableList()
        val selectedEntities = mutableListOf<EvaluatedQueens>()

        while (selectedEntities.size < numberOfEntitiesToLive) {
            val winner: EvaluatedQueens? = entitiesPool
                .takeRandom(hyperparameters.tournamentSize)
                .minBy { it.fitness }

            if (winner != null) {
                selectedEntities.add(winner)
                entitiesPool.remove(winner)
            }
        }

        return EvaluatedPopulation(selectedEntities)
    }
}