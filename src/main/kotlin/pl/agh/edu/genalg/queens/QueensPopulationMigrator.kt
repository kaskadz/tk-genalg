package pl.agh.edu.genalg.queens

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import pl.agh.edu.genalg.framework.MigrationMessage
import pl.agh.edu.genalg.framework.flow.PopulationMigrator
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.MigrationSelection
import pl.agh.edu.genalg.framework.model.SemiEvaluatedPopulation
import kotlin.math.roundToInt

class QueensPopulationMigrator(
    hyperparameters: QueensHyperparameters,
    reporter: Reporter,
    immigrantsChannel: ReceiveChannel<MigrationMessage<Queens>>,
    emigrantsChannel: SendChannel<MigrationMessage<Queens>>
) : PopulationMigrator<Queens, EvaluatedQueens, QueensHyperparameters>(
    hyperparameters,
    reporter,
    immigrantsChannel,
    emigrantsChannel
) {
    override fun shouldMigrate(
        iterationCount: Int,
        semiEvaluatedPopulation: SemiEvaluatedPopulation<Queens, EvaluatedQueens>
    ): Boolean {
        return iterationCount % hyperparameters.iterationsCountBetweenMigrations == 0
    }

    override fun selectEmigrants(population: SemiEvaluatedPopulation<Queens, EvaluatedQueens>): MigrationSelection<Queens> {
        val numberOfEntitiesToMigrate = (population.evaluatedEntities.size * hyperparameters.migrationRate).roundToInt()
        val sortedByScore = population.evaluatedEntities.sortedBy { it.fitness }

        val emigrants = sortedByScore
            .slice(0 until numberOfEntitiesToMigrate)
            .map { it.entity }
        val nonMigrants = sortedByScore
            .slice(numberOfEntitiesToMigrate until sortedByScore.size)
            .map { it.entity }

        return MigrationSelection(emigrants, nonMigrants + population.entities)
    }
}