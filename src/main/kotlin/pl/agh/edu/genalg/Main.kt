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
//    val population = populationInitializer.initializePopulation()
//    var iterationCount = 0
//    val evaluatedEntities = evaluator.evaluatePopulation(population)
//    var evaluatedPopulation = evaluator.sortPopulation(evaluatedEntities)
//    while (!stopCondition.shouldStop(iterationCount++, evaluatedPopulation)) {
//        val selected = selector.selectPopulation(evaluatedPopulation)
//        val entitiesToRecombine = recombinator.selectPopulationToBeRecombined(selected)
//        val newEntities = recombinator.recombinePopulation(entitiesToRecombine)
//        val oldMutatedPopulation = selected.evaluatedEntities.map { mutator.mutateEntity(it.entity) }
//        val newPopulation = newEntities.entities + oldMutatedPopulation
//        val evaluatedEntities = evaluator.evaluatePopulation(
//            Population(
//                newPopulation
//            )
//        )
//        evaluatedPopulation = evaluator.sortPopulation(evaluatedEntities)
//    }

    println("fin")
    println(evaluatedPopulation.evaluatedEntities.first())
}