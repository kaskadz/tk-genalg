package pl.agh.edu.genalg.model

import pl.agh.edu.genalg.framework.Population
import pl.agh.edu.genalg.framework.PopulationInitializer

class ArcherPopulationInitializer(hyperparameters: ArcherHyperparameters) :
    PopulationInitializer<Archer, ArcherHyperparameters>(hyperparameters) {

    override fun initializePopulation(): Population<Archer> {
        val listOfArchers = (0..hyperparameters.initialPopulationSize).map {
            val velocity = (0..hyperparameters.maxInitialVelocity).random()
            val angle = (0..90).random()
            Archer(velocity, angle)
        }.toList()

        return Population(listOfArchers)
    }
}