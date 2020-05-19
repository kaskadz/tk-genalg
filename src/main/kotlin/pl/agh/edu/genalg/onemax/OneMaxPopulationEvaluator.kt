package pl.agh.edu.genalg.onemax

import pl.agh.edu.genalg.framework.flow.PopulationEvaluator
import pl.agh.edu.genalg.framework.model.EvaluatedEntity

class OneMaxPopulationEvaluator(hyperparameters: OneMaxHyperparameters) :
    PopulationEvaluator<BinaryVector, EvaluatedBinaryVector, OneMaxHyperparameters>(hyperparameters) {

    override fun evaluateEntity(entity: BinaryVector): EvaluatedBinaryVector {
        val numberOfOnes = entity.genes.count { it == 1 }
        return EvaluatedBinaryVector(entity, numberOfOnes)
    }
}