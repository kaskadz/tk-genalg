package pl.agh.edu.genalg

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import pl.agh.edu.genalg.framework.SupervisorActor
import pl.agh.edu.genalg.framework.flow.MaxIterationsStopCondition
import pl.agh.edu.genalg.onemax.*

@ExperimentalCoroutinesApi
fun main() {
    val hyperparameters = OneMaxHyperparameters(
        maxIterationsCount = 105,
        initialPopulationSize = 100,
        vectorSize = 100,
        deathRate = 0.3,
        reproductionRate = 0.3,
        migrationRate = 0.1,
        mutationMaxScope = 0.15,
        mutationRate = 0.5,
        iterationsCountBetweenMigrations = 20
    )

    val supervisorActor = SupervisorActor(
        hyperparameters,
        { h -> OneMaxPopulationInitializer(h) },
        { h -> OneMaxPopulationEvaluator(h) },
        { h -> MaxIterationsStopCondition(h) },
        { h -> OneMaxPopulationSelector(h) },
        { h -> OneMaxPopulationRecombinator(h) },
        { h -> OneMaxPopulationMutator(h) },
        { h, c1, c2 -> OneMaxPopulationMigrator(h, c1, c2) }
    )

    println("start")

    runBlocking {
        supervisorActor.runSimulation(5) { results ->
            results
                .sortedByDescending { it.numberOfOnes }
                .forEach { println(it) }
        }
    }

    println("finished")

    OneMaxPopulationInitializer(hyperparameters)
        .initializePopulation()
        .entities
        .map { e -> e.genes.count { it == 1 } }
        .sortedByDescending { it }
        .take(10)
        .forEach { println(it) }
}