package pl.agh.edu.genalg.framework

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import pl.agh.edu.genalg.framework.flow.*
import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity
import pl.agh.edu.genalg.framework.model.Hyperparameters
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

class IslandActor<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    val id: Int,
    val hyperparameters: H,
    private val resultChannel: SendChannel<ResultsMessage<E, F>>,
    private val metricsChannel: SendChannel<Metric>,
    private val immigrantsChannel: Channel<MigrationMessage<E>>,
    private val emigrantsChannel: Channel<MigrationMessage<E>>,
    private val coroutineScope: CoroutineScope,
    private val populationInitializer: PopulationInitializer<E, H>,
    private val populationEvaluator: PopulationEvaluator<E, F, H>,
    private val stopCondition: StopCondition<E, F, H>,
    private val populationSelector: PopulationSelector<E, F, H>,
    private val populationRecombinator: PopulationRecombinator<E, F, H>,
    private val populationMutator: PopulationMutator<E, F, H>,
    populationMigratorFactory: (H, ReceiveChannel<MigrationMessage<E>>, SendChannel<MigrationMessage<E>>) -> PopulationMigrator<E, F, H>
) {
    val immigrantsInputChannel: SendChannel<MigrationMessage<E>> = immigrantsChannel

    private val populationMigrator =
        populationMigratorFactory(hyperparameters, immigrantsChannel, emigrantsChannel)

    @ExperimentalTime
    @ExperimentalCoroutinesApi
    fun start() = coroutineScope.launch(Dispatchers.Unconfined) {
        println("Actor($id) started")
        var iterationCount = 0

        val population = populationInitializer.initializePopulation()
        sendMetric(iterationCount, "initialPopulation", population.entities.size)

        var evaluatedPopulation = populationEvaluator.evaluatePopulation(population)
        sendMetric(iterationCount, "postEvaluationPopulationSize", evaluatedPopulation.evaluatedEntities.size)

        while (isActive && !stopCondition.shouldStop(++iterationCount, evaluatedPopulation)) {
            val selectedPopulation = populationSelector.selectPopulation(evaluatedPopulation)
            sendMetric(iterationCount, "postSelectionPopulationSize", selectedPopulation.evaluatedEntities.size)
            sendMetric(
                iterationCount,
                "entitiesDied",
                evaluatedPopulation.evaluatedEntities.size - selectedPopulation.evaluatedEntities.size
            )

            val postRecombinationPopulation = populationRecombinator.recombinePopulation(selectedPopulation)
            sendMetric(iterationCount, "postRecombinationPopulationSize", postRecombinationPopulation.entities.size)
            sendMetric(
                iterationCount,
                "entitiesBorn",
                postRecombinationPopulation.entities.size - selectedPopulation.evaluatedEntities.size
            )

            val postMutationPopulation = populationMutator.mutatePopulation(postRecombinationPopulation)
            sendMetric(iterationCount, "postMutationPopulationSize", postMutationPopulation.entities.size)

            val postMigrationPopulation = populationMigrator.applyMigration(id, iterationCount, postMutationPopulation)
            sendMetric(iterationCount, "postMigrationPopulationSize", postMigrationPopulation.entities.size)

            evaluatedPopulation = populationEvaluator.evaluatePopulation(postMigrationPopulation)
            sendMetric(iterationCount, "postEvaluationPopulationSize", evaluatedPopulation.evaluatedEntities.size)
            delay((0..10).random().milliseconds)
        }
        println("Actor($id) finished in $iterationCount iteration; populationSize = ${evaluatedPopulation.evaluatedEntities.size}")

        resultChannel.send(ResultsMessage(evaluatedPopulation.evaluatedEntities))

        for (immigrants in immigrantsChannel) {
            try {
                emigrantsChannel.send(MigrationMessage(id, immigrants.migrants))
            } catch (e: ClosedSendChannelException) {
                if (immigrants.migrants.any()) {
                    println("Actor($id) discarding ${immigrants.migrants.size} migrants. Emigrants channel closed.")
                }
            }
        }
    }

    private suspend fun sendMetric(iteration: Int, key: String, value: Any) {
        metricsChannel.send(Metric(iteration, id, key, value))
    }
}