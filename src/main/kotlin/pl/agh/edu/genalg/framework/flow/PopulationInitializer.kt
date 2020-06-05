package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.Hyperparameters
import pl.agh.edu.genalg.framework.model.Population
import pl.agh.edu.genalg.onemax.BinaryVector

abstract class PopulationInitializer<E : Entity, H : Hyperparameters>(val hyperparameters: H, val reporter: Reporter) {
    fun initializePopulation(): Population<E> {
        val entities = (1..hyperparameters.initialPopulationSize)
            .map { initializeEntity() }

        return Population(entities)
    }

    protected abstract fun initializeEntity(): E
}