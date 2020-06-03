package pl.agh.edu.genalg.framework.flow

import pl.agh.edu.genalg.framework.Reporter
import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.Hyperparameters
import pl.agh.edu.genalg.framework.model.Population

abstract class PopulationInitializer<E : Entity, H : Hyperparameters>(val hyperparameters: H, val reporter: Reporter) {
    abstract fun initializePopulation(): Population<E>
}