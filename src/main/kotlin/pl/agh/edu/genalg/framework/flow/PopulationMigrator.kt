package pl.agh.edu.genalg.framework.flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation
import pl.agh.edu.genalg.framework.model.Hyperparameters

abstract class PopulationMigrator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    val hyperparameters: H,
    private val immigrantsChannel: ReceiveChannel<Collection<F>>,
    private val emigrantsChannel: SendChannel<Collection<F>>
) {
    protected abstract fun shouldMigrate(iterationCount: Int): Boolean
    protected abstract fun selectEmigrants(evaluatedPopulation: EvaluatedPopulation<E, F>): Collection<F>

    @ExperimentalCoroutinesApi
    suspend fun applyMigration(
        iterationCount: Int,
        evaluatedPopulation: EvaluatedPopulation<E, F>
    ): Collection<E> {
        if (shouldMigrate(iterationCount)) {
            val emigrants = selectEmigrants(evaluatedPopulation)
            emigrantsChannel.send(emigrants)
        }

        if (!immigrantsChannel.isEmpty) {
            val immigrants = immigrantsChannel.receive()
            return immigrants.map { it.entity }
        }

        return emptyList()
    }
}