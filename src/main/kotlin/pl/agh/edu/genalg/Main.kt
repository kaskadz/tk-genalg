package pl.agh.edu.genalg

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import pl.agh.edu.genalg.framework.SupervisorActor
import pl.agh.edu.genalg.onemax.*
import pl.agh.edu.genalg.queens.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
fun main() {
    println("start")

//    oneMax()
    queens()

    println("finished")
}

@ExperimentalCoroutinesApi
@ExperimentalTime
private fun queens() {
    val hyperparameters = QueensHyperparameters(
        maxIterationsCount = 1000,
        initialPopulationSize = 100,
        deathRate = 0.3,
        reproductionRate = 0.3 / 0.7,
        migrationRate = 0.1,
        mutationRate = 0.5,
        iterationsCountBetweenMigrations = 10,
        minimalPopulationSize = 2,
        maxNumberOfQueensToMutate = 1
    )

    val supervisorActor = SupervisorActor(
        hyperparameters,
        { h, r -> QueensPopulationInitializer(h, r) },
        { h, r -> QueensPopulationEvaluator(h, r) },
        { h, r -> QueensStopCondition(h, r) },
        { h, r -> QueensPopulationSelector(h, r) },
        { h, r -> QueensPopulationRecombinator(h, r) },
        { h, r -> QueensPopulationMutator(h, r) },
        { h, r, c1, c2 -> QueensPopulationMigrator(h, r, c1, c2) },
        { h, r -> QueensResultHandler(h, r) },
        { h, r -> QueensIterationReporter(h, r) }
    )

    runBlocking {
        supervisorActor.runSimulation(5) { results ->
            results
                .sortedBy { it.fitness }
                .take(5)
                .forEach {
                    println(it.entity)
                    println("F = ${it.fitness}")
                }
        }
    }
}

@ExperimentalCoroutinesApi
@ExperimentalTime
private fun oneMax() {
    val hyperparameters = OneMaxHyperparameters(
        maxIterationsCount = 1000,
        initialPopulationSize = 1000,
        vectorSize = 100,
        deathRate = 0.3,
        reproductionRate = 0.3 / 0.7,
        migrationRate = 0.1,
        mutationMaxScope = 0.05,
        mutationRate = 0.5,
        iterationsCountBetweenMigrations = 10,
        minimalPopulationSize = 2
    )

    val supervisorActor = SupervisorActor(
        hyperparameters,
        { h, r -> OneMaxPopulationInitializer(h, r) },
        { h, r -> OneMaxPopulationEvaluator(h, r) },
        { h, r -> OneMaxStopCondition(h, r) },
        { h, r -> OneMaxPopulationSelector(h, r) },
        { h, r -> OneMaxPopulationRecombinator(h, r) },
        { h, r -> OneMaxPopulationMutator(h, r) },
        { h, r, c1, c2 -> OneMaxPopulationMigrator(h, r, c1, c2) },
        { h, r -> OneMaxResultHandler(h, r) },
        { h, r -> OneMaxIterationReporter(h, r) }
    )

    runBlocking {
        supervisorActor.runSimulation(5) { results ->
            results
                .sortedByDescending { it.numberOfOnes }
                .forEach { println(it) }
        }
    }
}