package pl.agh.edu.genalg.framework.flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.isActive
import pl.agh.edu.genalg.framework.GenalgSimulationException
import pl.agh.edu.genalg.framework.MigrationMessage
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.*
import kotlin.coroutines.coroutineContext

abstract class PopulationMigrator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    val hyperparameters: H,
    val reporter: Reporter,
    private val immigrantsChannel: ReceiveChannel<MigrationMessage<E>>,
    private val emigrantsChannel: SendChannel<MigrationMessage<E>>
) {
    protected abstract fun shouldMigrate(
        iterationCount: Int,
        semiEvaluatedPopulation: SemiEvaluatedPopulation<E, F>
    ): Boolean

    protected abstract fun selectEmigrants(population: SemiEvaluatedPopulation<E, F>): MigrationSelection<E>

    @ExperimentalCoroutinesApi
    suspend fun applyMigration(
        actorId: Int,
        iterationCount: Int,
        semiEvaluatedPopulation: SemiEvaluatedPopulation<E, F>
    ): Population<E> {
        val postMigrationPopulation = mutableListOf<E>()
        if (semiEvaluatedPopulation.allEntities.any() && shouldMigrate(iterationCount, semiEvaluatedPopulation)) {
            val migrationSelection = selectEmigrants(semiEvaluatedPopulation)

            validateSelection(migrationSelection, semiEvaluatedPopulation)

            postMigrationPopulation.addAll(migrationSelection.nonMigrants)
            if (migrationSelection.migrants.any()) {
                try {
                    emigrantsChannel.send(MigrationMessage(actorId, migrationSelection.migrants))
                } catch (e: ClosedSendChannelException) {
                    if (!coroutineContext.isActive) {
                        reporter.log(
                            "Discarding ${migrationSelection.migrants.size} emigrants," +
                                    " emigrants channel was closed because of cancellation."
                        )
                    } else {
                        throw e
                    }
                }
            }
        } else {
            postMigrationPopulation.addAll(semiEvaluatedPopulation.allEntities)
        }

        suspend fun pollImmigrants(): MigrationMessage<E>? = runCatching {
            immigrantsChannel.poll()
        }.onFailure {
            when (it) {
                is ClosedSendChannelException -> {
                    if (!coroutineContext.isActive) {
                        reporter.log("Poll immigrants failed, because of cancellation")
                    } else {
                        throw it
                    }
                }
                else -> throw it
            }
        }.getOrNull()

        var immigrants = pollImmigrants()
        while (immigrants != null) {
            reporter.log("Receiving ${immigrants.migrants.size} immigrants from ${immigrants.senderId}")
            postMigrationPopulation.addAll(immigrants.migrants)
            immigrants = pollImmigrants()
        }

        return Population(postMigrationPopulation)
    }

    private fun validateSelection(
        migrationSelection: MigrationSelection<E>,
        semiEvaluatedPopulation: SemiEvaluatedPopulation<E, F>
    ) {
        val selectionEntitiesCount = with(migrationSelection) {
            migrants.size + nonMigrants.size
        }

        if (selectionEntitiesCount != semiEvaluatedPopulation.size) {
            throw GenalgSimulationException("Invalid migrant selection")
        }
    }
}