package pl.agh.edu.genalg.queens

import pl.agh.edu.genalg.framework.GenalgSimulationException
import pl.agh.edu.genalg.framework.flow.PopulationInitializer
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.Population

class QueensPopulationInitializer(hyperparameters: QueensHyperparameters, reporter: Reporter) :
    PopulationInitializer<Queens, QueensHyperparameters>(hyperparameters, reporter) {

    override fun initializePopulation(): Population<Queens> {
        val entities = (1..hyperparameters.initialPopulationSize)
            .map {
                val positions = Position.getRandom(QueensCount).toSet()

                if (positions.size != QueensCount)
                    throw GenalgSimulationException("Generated invalid number of queens.")

                Queens(positions)
            }

        return Population(entities)
    }
}