package pl.agh.edu.genalg.framework

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import pl.agh.edu.genalg.framework.flow.*
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
    private val populationMigratorFactory: (H, Reporter, ReceiveChannel<MigrationMessage<E>>, SendChannel<MigrationMessage<E>>) -> PopulationMigrator<E, F, H>
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
                        { reportContext -> ContextReporter(reportContext, metricsActor.metricsChannel) }
                    )
                }.toList()

            val metricsActorJob = metricsActor.start()
            val islandJobs = islandActors.map { it.start() }

            launch {
                for (emigrants in emigrantsChannel) {
                    val islandActorReceiver = islandActors
                        .find { it.id == (emigrants.senderId + 1) % islandsCount }

                    if (islandActorReceiver != null) {
                        println("Sending ${emigrants.migrants.size} to actor ${islandActorReceiver.id}")
                        islandActorReceiver.immigrantsInputChannel.send(emigrants)
                    } else {
                        throw GenalgSimulationException("Tried to send migrants to non existing actor")
                    }
                }
            }

            val resultList = resultChannel
                .take(islandsCount)
                .toList()
                .map { it.results }
                .flatten()
                .toList()

            islandActors.forEach { it.immigrantsInputChannel.close() }
            emigrantsChannel.close()
            islandJobs.forEach { it.join() }

            metricsActorJob.cancel()
            metricsActorJob.join()
            metricsActor.saveReport()

            handleResults(resultList)
        }
    }
}