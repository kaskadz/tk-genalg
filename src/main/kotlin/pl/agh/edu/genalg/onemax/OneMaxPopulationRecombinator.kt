package pl.agh.edu.genalg.onemax

import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.flow.CouplePopulationRecombinator
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation
import pl.agh.edu.genalg.framework.model.Population
import kotlin.math.roundToInt
import kotlin.random.Random

class OneMaxPopulationRecombinator(hyperparameters: OneMaxHyperparameters, reporter: Reporter) :
    CouplePopulationRecombinator<BinaryVector, EvaluatedBinaryVector, OneMaxHyperparameters>(
        hyperparameters,
        reporter
    ) {

    private val random = Random.Default

    override fun recombineCouple(entity1: BinaryVector, entity2: BinaryVector): BinaryVector {
        val cutPoint = random.nextInt(0, hyperparameters.vectorSize)
        val slice1 = entity1.genes.sliceArray(0 until cutPoint)
        val slice2 = entity2.genes.sliceArray(cutPoint until hyperparameters.vectorSize)

        return BinaryVector(slice1 + slice2)
    }

    override fun selectPopulationToBeRecombined(evaluatedPopulation: EvaluatedPopulation<BinaryVector, EvaluatedBinaryVector>): Population<BinaryVector> {
        val numberOfEntitiesChosenToRecombine =
            (evaluatedPopulation.size * 2 * hyperparameters.reproductionRate).roundToInt()

        val entitiesChosenToRecombine = evaluatedPopulation.evaluatedEntities
            .sortedByDescending { it.numberOfOnes }
            .take(numberOfEntitiesChosenToRecombine)
            .map { it.entity }

        return Population(entitiesChosenToRecombine)
    }
}