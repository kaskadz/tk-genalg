package pl.agh.edu.genalg.onemax

import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.flow.PopulationMutator
import kotlin.math.roundToInt
import kotlin.random.Random

class OneMaxPopulationMutator(hyperparameters: OneMaxHyperparameters, reporter: Reporter) :
    PopulationMutator<BinaryVector, EvaluatedBinaryVector, OneMaxHyperparameters>(hyperparameters, reporter) {

    private val random = Random.Default

    override fun mutateEntity(entity: BinaryVector): BinaryVector {
        return if (random.nextDouble() < hyperparameters.mutationRate) {
            val percentageOfGenesToMutate = random.nextDouble(hyperparameters.mutationMaxScope)
            val numberOfGenesToMutate = (hyperparameters.vectorSize * percentageOfGenesToMutate).roundToInt()

            repeat(numberOfGenesToMutate) {
                val geneToMutateIndex = random.nextInt(hyperparameters.vectorSize)
                val gene = entity.genes[geneToMutateIndex]
                entity.genes[geneToMutateIndex] = (gene + 1) % 2
            }

            entity
        } else {
            entity
        }
    }
}