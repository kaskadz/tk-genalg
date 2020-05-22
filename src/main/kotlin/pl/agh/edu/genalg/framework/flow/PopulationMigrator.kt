package pl.agh.edu.genalg.framework.flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import pl.agh.edu.genalg.framework.MigrationMessage
import pl.agh.edu.genalg.framework.model.*

abstract class PopulationMigrator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    val hyperparameters: H,
    private val immigrantsChannel: ReceiveChannel<MigrationMessage<E>>,
    private val emigrantsChannel: SendChannel<MigrationMessage<E>>
) {
    protected abstract fun shouldMigrate(iterationCount: Int): Boolean
    protected abstract fun selectEmigrants(population: Population<E>): Pair<Collection<E>, Collection<E>>

    @ExperimentalCoroutinesApi
    suspend fun applyMigration(
        actorId: Int,
        iterationCount: Int,
        evaluatedPopulation: Population<E>
    ): Population<E> {
        val postMigrationPopulation = mutableListOf<E>()
        if (evaluatedPopulation.entities.any() && shouldMigrate(iterationCount)) {
            val (emigrants, nonMigrants) = selectEmigrants(evaluatedPopulation)
            postMigrationPopulation.addAll(nonMigrants)
            if (emigrants.any()) {
                emigrantsChannel.send(MigrationMessage(actorId, emigrants))
            }
        } else {
            postMigrationPopulation.addAll(evaluatedPopulation.entities)
        }

        while (!immigrantsChannel.isEmpty) {
            val immigrants = immigrantsChannel.receive()
            postMigrationPopulation.addAll(immigrants.migrants)
        }

        return Population(postMigrationPopulation)
    }
}