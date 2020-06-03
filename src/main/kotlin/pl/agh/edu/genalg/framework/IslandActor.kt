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
    private val immigrantsChannel: Channel<MigrationMessage<E>>,
    private val emigrantsChannel: Channel<MigrationMessage<E>>,
    private val coroutineScope: CoroutineScope,
    populationInitializerFactory: (H, Reporter) -> PopulationInitializer<E, H>,
    populationEvaluatorFactory: (H, Reporter) -> PopulationEvaluator<E, F, H>,
    stopConditionFactory: (H, Reporter) -> StopCondition<E, F, H>,
    populationSelectorFactory: (H, Reporter) -> PopulationSelector<E, F, H>,
    populationRecombinatorFactory: (H, Reporter) -> PopulationRecombinator<E, F, H>,
    populationMutatorFactory: (H, Reporter) -> PopulationMutator<E, F, H>,
    populationMigratorFactory: (H, Reporter, ReceiveChannel<MigrationMessage<E>>, SendChannel<MigrationMessage<E>>) -> PopulationMigrator<E, F, H>,
    reporterFactory: (ReportContext) -> Reporter
) {
    val immigrantsInputChannel: SendChannel<MigrationMessage<E>> = immigrantsChannel

    private var iterationCount = 0

    private val reporter = reporterFactory(ReportContext(id) { this.iterationCount })
    private val populationInitializer = populationInitializerFactory(hyperparameters, reporter)
    private val populationEvaluator = populationEvaluatorFactory(hyperparameters, reporter)
    private val stopCondition = stopConditionFactory(hyperparameters, reporter)
    private val populationSelector = populationSelectorFactory(hyperparameters, reporter)
    private val populationRecombinator = populationRecombinatorFactory(hyperparameters, reporter)
    private val populationMutator = populationMutatorFactory(hyperparameters, reporter)
    private val populationMigrator =
        populationMigratorFactory(hyperparameters, reporter, immigrantsChannel, emigrantsChannel)

    @ExperimentalTime
    @ExperimentalCoroutinesApi
    fun start() = coroutineScope.launch(Dispatchers.Unconfined) {
        reporter.log("started")

        val population = populationInitializer.initializePopulation()
        reporter.metric("populationSize", population.entities.size)

        var evaluatedPopulation = populationEvaluator.evaluatePopulation(population)

        while (isActive && !stopCondition.shouldStop(++iterationCount, evaluatedPopulation)) {
            val selectedPopulation = populationSelector.selectPopulation(evaluatedPopulation)
            reporter.metric(
                "entitiesDied",
                evaluatedPopulation.evaluatedEntities.size - selectedPopulation.evaluatedEntities.size
            )

            val postRecombinationPopulation = populationRecombinator.recombinePopulation(selectedPopulation)
            reporter.metric(
                "entitiesBorn",
                postRecombinationPopulation.entities.size - selectedPopulation.evaluatedEntities.size
            )

            val postMutationPopulation = populationMutator.mutatePopulation(postRecombinationPopulation)

            val postMigrationPopulation = populationMigrator.applyMigration(id, iterationCount, postMutationPopulation)
            reporter.metric(
                "migrationDelta",
                postMigrationPopulation.entities.size - postMutationPopulation.entities.size
            )

            evaluatedPopulation = populationEvaluator.evaluatePopulation(postMigrationPopulation)
            reporter.metric("populationSize", evaluatedPopulation.evaluatedEntities.size)
            delay((0..10).random().milliseconds)
        }
        reporter.log("finished; populationSize = ${evaluatedPopulation.evaluatedEntities.size}")

        resultChannel.send(ResultsMessage(evaluatedPopulation.evaluatedEntities))

        for (immigrants in immigrantsChannel) {
            try {
                emigrantsChannel.send(MigrationMessage(id, immigrants.migrants))
            } catch (e: ClosedSendChannelException) {
                if (immigrants.migrants.any()) {
                    reporter.log("Discarding ${immigrants.migrants.size} migrants. Emigrants channel closed.")
                }
            }
        }
    }
}