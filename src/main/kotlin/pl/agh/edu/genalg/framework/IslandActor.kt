package pl.agh.edu.genalg.framework

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pl.agh.edu.genalg.framework.flow.*
import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity
import pl.agh.edu.genalg.framework.model.Hyperparameters

class IslandActor<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    val id: Int,
    val hyperparameters: H,
    private val resultChannel: SendChannel<ResultsMessage<E, F>>,
    private val immigrantsChannel: Channel<MigrationMessage<E>>,
    private val emigrantsChannel: Channel<MigrationMessage<E>>,
    private val coroutineScope: CoroutineScope,
    private val populationInitializer: PopulationInitializer<E, H>,
    private val populationEvaluator: PopulationEvaluator<E, F, H>,
    private val stopCondition: StopCondition<E, F, H>,
    private val populationSelector: PopulationSelector<E, F, H>,
    private val populationRecombinator: PopulationRecombinator<E, F, H>,
    private val populationMutator: PopulationMutator<E, F, H>,
    populationMigratorFactory: (H, ReceiveChannel<MigrationMessage<E>>, SendChannel<MigrationMessage<E>>) -> PopulationMigrator<E, H>
) {
    val immigrantsInputChannel: SendChannel<MigrationMessage<E>> = immigrantsChannel

    private val populationMigrator =
        populationMigratorFactory(hyperparameters, immigrantsChannel, emigrantsChannel)

    @ExperimentalCoroutinesApi
    fun start() = coroutineScope.launch {
        val population = populationInitializer.initializePopulation()
        var iterationCount = 0
        var evaluatedPopulation = populationEvaluator.evaluatePopulation(population)
        while (isActive && !stopCondition.shouldStop(++iterationCount, evaluatedPopulation)) {
            val selectedPopulation = populationSelector.selectPopulation(evaluatedPopulation)
            val postRecombinationPopulation = populationRecombinator.recombinePopulation(selectedPopulation)
            val postMutationPopulation = populationMutator.mutatePopulation(postRecombinationPopulation)
            val postMigrationPopulation = populationMigrator.applyMigration(id, iterationCount, postMutationPopulation)
            evaluatedPopulation = populationEvaluator.evaluatePopulation(postMigrationPopulation)
        }

        resultChannel.send(ResultsMessage(evaluatedPopulation.evaluatedEntities))

        for (immigrants in immigrantsChannel) {
            emigrantsChannel.send(MigrationMessage(id, immigrants.migrants))
        }
    }
}