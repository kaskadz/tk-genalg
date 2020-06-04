package pl.agh.edu.genalg.onemax

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import pl.agh.edu.genalg.framework.MigrationMessage
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.flow.PopulationMigrator
import pl.agh.edu.genalg.framework.model.MigrationSelection
import pl.agh.edu.genalg.framework.model.SemiEvaluatedPopulation
import kotlin.math.roundToInt

class OneMaxPopulationMigrator(
    hyperparameters: OneMaxHyperparameters,
    reporter: Reporter,
    immigrantsChannel: ReceiveChannel<MigrationMessage<BinaryVector>>,
    emigrantsChannel: SendChannel<MigrationMessage<BinaryVector>>
) : PopulationMigrator<BinaryVector, EvaluatedBinaryVector, OneMaxHyperparameters>(
    hyperparameters,
    reporter,
    immigrantsChannel,
    emigrantsChannel
) {
    override fun shouldMigrate(
        iterationCount: Int,
        semiEvaluatedPopulation: SemiEvaluatedPopulation<BinaryVector, EvaluatedBinaryVector>
    ): Boolean {
        return iterationCount % hyperparameters.iterationsCountBetweenMigrations == 0
    }

    override fun selectEmigrants(population: SemiEvaluatedPopulation<BinaryVector, EvaluatedBinaryVector>): MigrationSelection<BinaryVector> {
        val numberOfEntitiesToMigrate = (population.evaluatedEntities.size * hyperparameters.migrationRate).roundToInt()
        val sortedByScore = population.evaluatedEntities.sortedByDescending { it.numberOfOnes }

        val emigrants = sortedByScore
            .slice(0 until numberOfEntitiesToMigrate)
            .map { it.entity }
        val nonMigrants = sortedByScore
            .slice(numberOfEntitiesToMigrate until sortedByScore.size)
            .map { it.entity }

        return MigrationSelection(emigrants, nonMigrants + population.entities)
    }
}