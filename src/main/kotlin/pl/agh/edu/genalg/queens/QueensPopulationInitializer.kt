package pl.agh.edu.genalg.queens

import pl.agh.edu.genalg.framework.GenalgSimulationException
import pl.agh.edu.genalg.framework.flow.PopulationInitializer
import pl.agh.edu.genalg.framework.metrics.Reporter

class QueensPopulationInitializer(hyperparameters: QueensHyperparameters, reporter: Reporter) :
    PopulationInitializer<Queens, QueensHyperparameters>(hyperparameters, reporter) {

    override fun initializeEntity(): Queens {
        val positions = Position.getRandom(hyperparameters.boardSize, hyperparameters.boardSize).toSet()

        if (positions.size != hyperparameters.boardSize)
            throw GenalgSimulationException("Generated invalid number of queens.")

        return Queens(positions, hyperparameters.boardSize)
    }
}