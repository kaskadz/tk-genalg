package pl.agh.edu.genalg.framework

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import pl.agh.edu.genalg.framework.flow.*
import pl.agh.edu.genalg.framework.metrics.FacilityContextReporter
import pl.agh.edu.genalg.framework.metrics.IslandContextReporter
import pl.agh.edu.genalg.framework.metrics.MetricsActor
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity
import pl.agh.edu.genalg.framework.model.Hyperparameters
import kotlin.time.ExperimentalTime

class SupervisorActor<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    val hyperparameters: H,
    private val populationInitializerFactory: (H, Reporter) -> PopulationInitializer<E, H>,
    private val populationEvaluatorFactory: (H, Reporter) -> PopulationEvaluator<E, F, H>,
    private val stopConditionFactory: (H, Reporter) -> StopCondition<E, F, H>,
    private val populationSelectorFactory: (H, Reporter) -> PopulationSelector<E, F, H>,
    private val populationRecombinatorFactory: (H, Reporter) -> PopulationRecombinator<E, F, H>,
    private val populationMutatorFactory: (H, Reporter) -> PopulationMutator<E, F, H>,
    private val populationMigratorFactory: (H, Reporter, ReceiveChannel<MigrationMessage<E>>, SendChannel<MigrationMessage<E>>) -> PopulationMigrator<E, F, H>,
    private val resultHandlerFactory: (H, Reporter) -> ResultHandler<E, F, H>
) {

    @ExperimentalTime
    @ExperimentalCoroutinesApi
    suspend fun runSimulation(islandsCount: Int, handleResults: (List<F>) -> Unit) {
        coroutineScope {
            val metricsActor = MetricsActor(this)

            val resultChannel = Channel<ResultsMessage<E, F>>(Channel.UNLIMITED)
            val emigrantsChannel = Channel<MigrationMessage<E>>(Channel.UNLIMITED)
            val islandActors = (0 until islandsCount)
                .map { id ->
                    IslandActor(
                        id,
                        hyperparameters,
                        resultChannel,
                        Channel(Channel.UNLIMITED),
                        emigrantsChannel,
                        this,
                        populationInitializerFactory,
                        populationEvaluatorFactory,
                        stopConditionFactory,
                        populationSelectorFactory,
                        populationRecombinatorFactory,
                        populationMutatorFactory,
                        populationMigratorFactory,
                        resultHandlerFactory,
                        { reportContext ->
                            IslandContextReporter(
                                reportContext,
                                metricsActor.metricsChannel
                            )
                        }
                    )
                }.associateBy { it.id }

            val metricsActorJob = metricsActor.start()
            val islandJobs = islandActors.values.associate { it.id to it.start() }

            launch {
                val routerReporter = FacilityContextReporter("R", metricsActor.metricsChannel)
                for (emigrants in emigrantsChannel) {
                    val islandActorReceiver = islandActors[(emigrants.senderId + 1) % islandsCount]

                    if (islandActorReceiver != null) {
                        try {
                            islandActorReceiver.immigrantsInputChannel.send(emigrants)
                            routerReporter.log("Sent ${emigrants.migrants.size} migrants to actor ${islandActorReceiver.id}")
                        } catch (e: ClosedSendChannelException) {
                            if (islandJobs[islandActorReceiver.id]?.isCancelled ?: false) {
                                routerReporter.log(
                                    "Discarding ${emigrants.migrants.size} migrants meant to be sent to actor ${islandActorReceiver.id}." +
                                            " Immigrants channel was closed because of cancellation."
                                )
                            }
                        }
                    } else {
                        throw GenalgSimulationException("Tried to send migrants to non existing actor")
                    }
                }
            }

            var finishedActorsCount = 0
            val resultList = mutableListOf<F>()
            loop@ for (result in resultChannel) {
                finishedActorsCount++;
                resultList.addAll(result.results)
                when (result) {
                    is FinishedResultsMessage -> {
                        if (finishedActorsCount >= islandsCount) {
                            break@loop
                        }
                    }
                    is FinishedAndStopAllResultsMessage -> {
                        islandJobs.values.forEach { it.cancelAndJoin() }
                        break@loop
                    }
                }
            }

            islandActors.values.forEach { it.immigrantsInputChannel.close() }
            emigrantsChannel.close()
            islandJobs.values.forEach { it.join() }

            metricsActorJob.cancel()
            metricsActorJob.join()
            metricsActor.saveReport()

            handleResults(resultList)
        }
    }
}