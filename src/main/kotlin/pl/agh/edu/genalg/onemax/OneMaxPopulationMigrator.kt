package pl.agh.edu.genalg.onemax

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import pl.agh.edu.genalg.framework.MigrationMessage
import pl.agh.edu.genalg.framework.flow.PopulationMigrator
import pl.agh.edu.genalg.framework.model.Population
import kotlin.math.roundToInt

class OneMaxPopulationMigrator(
    hyperparameters: OneMaxHyperparameters,
    immigrantsChannel: ReceiveChannel<MigrationMessage<BinaryVector>>,
    emigrantsChannel: SendChannel<MigrationMessage<BinaryVector>>
) : PopulationMigrator<BinaryVector, EvaluatedBinaryVector, OneMaxHyperparameters>(
    hyperparameters,
    immigrantsChannel,
    emigrantsChannel
) {
    override fun shouldMigrate(iterationCount: Int): Boolean {
        return iterationCount % hyperparameters.iterationsCountBetweenMigrations == 0
    }

    override fun selectEmigrants(population: Population<BinaryVector>): Pair<Collection<BinaryVector>, Collection<BinaryVector>> {
        val numberOfEntitiesToMigrate = (population.entities.size * hyperparameters.migrationRate).roundToInt()

        val sortedByScore = population.entities.shuffled()

        return Pair(
            sortedByScore.slice(0 until numberOfEntitiesToMigrate),
            sortedByScore.slice(numberOfEntitiesToMigrate until sortedByScore.size)
        )
    }
}