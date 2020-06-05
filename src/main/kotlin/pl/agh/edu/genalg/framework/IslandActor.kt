package pl.agh.edu.genalg.framework

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import pl.agh.edu.genalg.framework.flow.*
import pl.agh.edu.genalg.framework.metrics.IslandReportContext
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity
import pl.agh.edu.genalg.framework.model.Hyperparameters
import kotlin.time.ExperimentalTime
import kotlin.time.nanoseconds

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
    resultHandlerFactory: (H, Reporter) -> ResultHandler<E, F, H>,
    iterationReporterFactory: (H, Reporter) -> IterationReporter<E, F, H>,
    reporterFactory: (IslandReportContext) -> Reporter
) {
    val immigrantsInputChannel: SendChannel<MigrationMessage<E>> = immigrantsChannel

    private var iterationCount = 0

    private val reporter = reporterFactory(IslandReportContext(id) { this.iterationCount })
    private val populationInitializer = populationInitializerFactory(hyperparameters, reporter)
    private val populationEvaluator = populationEvaluatorFactory(hyperparameters, reporter)
    private val stopCondition = stopConditionFactory(hyperparameters, reporter)
    private val populationSelector = populationSelectorFactory(hyperparameters, reporter)
    private val populationRecombinator = populationRecombinatorFactory(hyperparameters, reporter)
    private val populationMutator = populationMutatorFactory(hyperparameters, reporter)
    private val populationMigrator =
        populationMigratorFactory(hyperparameters, reporter, immigrantsChannel, emigrantsChannel)
    private val resultHandler = resultHandlerFactory(hyperparameters, reporter)
    private val iterationReporter = iterationReporterFactory(hyperparameters, reporter)

    @ExperimentalTime
    @ExperimentalCoroutinesApi
    fun start() = coroutineScope.launch(Dispatchers.Unconfined) {
        try {
            reporter.log("started")

            val population = populationInitializer.initializePopulation()
            reporter.metric("populationSize", population.size)

            var evaluatedPopulation = populationEvaluator.evaluatePopulation(population)

            var shouldAllStop = false
            while (isActive
                && !stopCondition.shouldAllStop(++iterationCount, evaluatedPopulation)
                    .also { shouldAllStop = it }
                && !stopCondition.shouldStop(iterationCount, evaluatedPopulation)
            ) {
                val selectedPopulation = populationSelector.selectPopulation(evaluatedPopulation)
                reporter.metric(
                    "entitiesDied",
                    evaluatedPopulation.size - selectedPopulation.size
                )

                val postRecombinationPopulation = populationRecombinator.recombinePopulation(selectedPopulation)
                reporter.metric(
                    "entitiesBorn",
                    postRecombinationPopulation.size - selectedPopulation.size
                )

                val postMigrationPopulation =
                    populationMigrator.applyMigration(id, iterationCount, postRecombinationPopulation)
                reporter.metric(
                    "migrationDelta",
                    postMigrationPopulation.size - postRecombinationPopulation.size
                )

                val postMutationPopulation = populationMutator.mutatePopulation(postMigrationPopulation)

                evaluatedPopulation = populationEvaluator.evaluatePopulation(postMutationPopulation)
                reporter.metric("populationSize", evaluatedPopulation.size)

                iterationReporter.report(iterationCount, evaluatedPopulation)

                delay((0..10).random().nanoseconds)
            }
            reporter.log("finished; populationSize = ${evaluatedPopulation.size}")
            if (shouldAllStop) {
                reporter.log("stopAll")
            }

            val resultsMessage = resultHandler.selectResults(evaluatedPopulation)
                .let { if (shouldAllStop) FinishedAndStopAllResultsMessage(it) else FinishedResultsMessage(it) }

            resultChannel.send(resultsMessage)

            for (immigrants in immigrantsChannel) {
                try {
                    emigrantsChannel.send(MigrationMessage(id, immigrants.migrants))
                } catch (e: ClosedSendChannelException) {
                    if (immigrants.migrants.any() && !isActive) {
                        reporter.log(
                            "Discarding ${immigrants.migrants.size} migrants." +
                                    " Emigrants channel closed because of cancellation."
                        )
                    }
                }
            }
        } finally {
            reporter.log("stopped")
        }
    }
}