package pl.agh.edu.genalg.framework.flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import pl.agh.edu.genalg.framework.MigrationMessage
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity
import pl.agh.edu.genalg.framework.model.Hyperparameters
import pl.agh.edu.genalg.framework.model.Population

abstract class PopulationMigrator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    val hyperparameters: H,
    val reporter: Reporter,
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

        fun pollImmigrants() = immigrantsChannel.poll()

        var immigrants = pollImmigrants()
        while (immigrants != null) {
            println("Receiving ${immigrants.migrants.size} immigrants from ${immigrants.senderId}")
            postMigrationPopulation.addAll(immigrants.migrants)
            immigrants = pollImmigrants()
        }

        return Population(postMigrationPopulation)
    }
}