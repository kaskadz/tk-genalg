package pl.agh.edu.genalg.onemax

import pl.agh.edu.genalg.framework.Reporter
import pl.agh.edu.genalg.framework.flow.PopulationEvaluator

class OneMaxPopulationEvaluator(hyperparameters: OneMaxHyperparameters, reporter: Reporter) :
    PopulationEvaluator<BinaryVector, EvaluatedBinaryVector, OneMaxHyperparameters>(hyperparameters, reporter) {

    override fun evaluateEntity(entity: BinaryVector): EvaluatedBinaryVector {
        val numberOfOnes = entity.genes.count { it == 1 }
        return EvaluatedBinaryVector(entity, numberOfOnes)
    }
}