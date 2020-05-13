package pl.agh.edu.genalg

import pl.agh.edu.genalg.framework.flow.MaxIterationsStopCondition
import pl.agh.edu.genalg.framework.model.Population
import pl.agh.edu.genalg.model.*

fun main() {
    val hyperparameters = ArcherHyperparameters(
        maxIterationsCount = 20,
        initialPopulationSize = 100,
        maxInitialVelocity = 100,
        targetRange = 800.0,
        maxAbsError = 500.0
    )

    val populationInitializer = ArcherPopulationInitializer(hyperparameters)
    val evaluator = ArcherEvaluator(hyperparameters)
    val stopCondition =
        MaxIterationsStopCondition<Archer, EvaluatedArcher, ArcherHyperparameters>(
            hyperparameters
        )
    val selector = ArcherPopulationSelector(hyperparameters)
    val recombinator = ArcherRecombinator(hyperparameters)
    val mutator = ArcherMutator(hyperparameters)

    println("hello")
}