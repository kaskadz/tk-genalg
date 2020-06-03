package pl.agh.edu.genalg.onemax

import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.flow.PopulationInitializer
import pl.agh.edu.genalg.framework.model.Population

class OneMaxPopulationInitializer(hyperparameters: OneMaxHyperparameters, reporter: Reporter) :
    PopulationInitializer<BinaryVector, OneMaxHyperparameters>(hyperparameters, reporter) {

    private val random = kotlin.random.Random.Default

    override fun initializePopulation(): Population<BinaryVector> {
        val entities = (1..hyperparameters.initialPopulationSize)
            .map {
                val randomVector = (1..hyperparameters.vectorSize)
                    .map { getRandomGene() }
                    .toIntArray()

                BinaryVector(randomVector)
            }

        return Population(entities)
    }

    private fun getRandomGene(): Int {
        return if (random.nextDouble() < 0.5) 1 else 0
    }
}