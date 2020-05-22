package pl.agh.edu.genalg

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import pl.agh.edu.genalg.framework.SupervisorActor
import pl.agh.edu.genalg.onemax.*

@ExperimentalCoroutinesApi
fun main() {
    val hyperparameters = OneMaxHyperparameters(
        maxIterationsCount = 100000,
        initialPopulationSize = 100,
        vectorSize = 100,
        deathRate = 0.4,
        reproductionRate = 0.4,
        migrationRate = 0.1,
        mutationMaxScope = 0.05,
        mutationRate = 0.5,
        iterationsCountBetweenMigrations = 20,
        minimalPopulationSize = 2
    )

    val supervisorActor = SupervisorActor(
        hyperparameters,
        { h -> OneMaxPopulationInitializer(h) },
        { h -> OneMaxPopulationEvaluator(h) },
        { h -> OneMaxStopCondition(h) },
        { h -> OneMaxPopulationSelector(h) },
        { h -> OneMaxPopulationRecombinator(h) },
        { h -> OneMaxPopulationMutator(h) },
        { h, c1, c2 -> OneMaxPopulationMigrator(h, c1, c2) }
    )

    println("start")

    runBlocking {
        supervisorActor.runSimulation(10) { results ->
            results
                .sortedByDescending { it.numberOfOnes }
                .take(10)
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