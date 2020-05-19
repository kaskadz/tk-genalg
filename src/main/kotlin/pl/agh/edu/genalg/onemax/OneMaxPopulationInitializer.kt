package pl.agh.edu.genalg.onemax

import pl.agh.edu.genalg.framework.flow.PopulationInitializer
import pl.agh.edu.genalg.framework.model.Population

class OneMaxPopulationInitializer(hyperparameters: OneMaxHyperparameters) :
    PopulationInitializer<BinaryVector, OneMaxHyperparameters>(hyperparameters) {

    private val random = kotlin.random.Random.Default

    override fun initializePopulation(): Population<BinaryVector> {
        val entities = (1..hyperparameters.initialPopulationSize)
            .map {
                val randomVector = (1..hyperparameters.vectorSize)
                    .map { random.nextInt() % 2 }
                    .toIntArray()

                BinaryVector(randomVector)
            }

        return Population(entities)
    }
}