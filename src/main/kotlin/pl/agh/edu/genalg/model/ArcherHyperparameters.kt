package pl.agh.edu.genalg.model

import pl.agh.edu.genalg.framework.Hyperparameters

class ArcherHyperparameters(
    maxIterationsCount: Int,
    val initialPopulationSize: Int,
    val maxInitialVelocity: Int,
    val targetRange: Double,
    val maxAbsError: Double
) : Hyperparameters(maxIterationsCount)