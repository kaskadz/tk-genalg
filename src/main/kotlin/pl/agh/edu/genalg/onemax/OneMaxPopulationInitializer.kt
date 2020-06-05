package pl.agh.edu.genalg.onemax

import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.flow.PopulationInitializer
import kotlin.random.Random

class OneMaxPopulationInitializer(hyperparameters: OneMaxHyperparameters, reporter: Reporter) :
    PopulationInitializer<BinaryVector, OneMaxHyperparameters>(hyperparameters, reporter) {

    override fun initializeEntity(): BinaryVector {
        val randomVector = (1..hyperparameters.vectorSize)
            .map { getRandomGene() }
            .toIntArray()

        return BinaryVector(randomVector)
    }

    private fun getRandomGene(): Int {
        return if (Random.nextDouble() < 0.5) 1 else 0
    }
}