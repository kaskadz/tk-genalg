package pl.agh.edu.genalg.model

import pl.agh.edu.genalg.framework.EvaluatedPopulation
import pl.agh.edu.genalg.framework.SingleEntityEvaluator
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin

class ArcherEvaluator(hyperparameters: ArcherHyperparameters) :
    SingleEntityEvaluator<Archer, EvaluatedArcher, ArcherHyperparameters>(hyperparameters) {

    private val g = 9.81

    override fun evaluateEntity(entity: Archer): EvaluatedArcher {
        val range = (entity.velocity.toDouble().pow(2) / g) * sin(2 * Math.toRadians(entity.angle.toDouble()))
        val error = hyperparameters.targetRange - range
        return EvaluatedArcher(entity, error)
    }

    override fun sortPopulation(evaluatedEntities: Collection<EvaluatedArcher>): EvaluatedPopulation<Archer, EvaluatedArcher> {
        val archersFromBestToWorst = evaluatedEntities
            .sortedByDescending { abs(it.error) }
            .toList()

        return EvaluatedPopulation(archersFromBestToWorst)
    }
}