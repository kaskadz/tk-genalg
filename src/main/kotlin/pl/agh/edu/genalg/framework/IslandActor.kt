package pl.agh.edu.genalg.framework

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pl.agh.edu.genalg.framework.flow.*
import pl.agh.edu.genalg.framework.model.*

class IslandActor<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(
    val hyperparameters: H,
    private val resultChannel: SendChannel<EvaluatedPopulation<E, F>>,
    private val coroutineScope: CoroutineScope,
    private val populationInitializer: PopulationInitializer<E, H>,
    private val populationEvaluator: PopulationEvaluator<E, F, H>,
    private val stopCondition: StopCondition<E, F, H>,
    private val populationSelector: PopulationSelector<E, F, H>,
    private val populationRecombinator: PopulationRecombinator<E, F, H>,
    private val populationMutator: PopulationMutator<E, F, H>,
    private val populationMigrator: PopulationMigrator<E, H>
) {

    @ExperimentalCoroutinesApi
    fun start() = coroutineScope.launch {
        val population = populationInitializer.initializePopulation()
        var iterationCount = 0
        var evaluatedPopulation = populationEvaluator.evaluatePopulation(population)
        while (isActive && !stopCondition.shouldStop(++iterationCount, evaluatedPopulation)) {
            val selectedPopulation = populationSelector.selectPopulation(evaluatedPopulation)
            val postRecombinationPopulation = populationRecombinator.recombinePopulation(selectedPopulation)
            val postMutationPopulation = populationMutator.mutatePopulation(postRecombinationPopulation)
            val postMigrationPopulation = populationMigrator.applyMigration(iterationCount, postMutationPopulation)
            evaluatedPopulation = populationEvaluator.evaluatePopulation(postMigrationPopulation)
        }
        resultChannel.send(evaluatedPopulation)
    }
}