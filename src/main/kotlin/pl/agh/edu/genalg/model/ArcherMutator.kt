package pl.agh.edu.genalg.model

import pl.agh.edu.genalg.framework.EvaluatedPopulation
import pl.agh.edu.genalg.framework.PopulationMutator
import kotlin.random.Random

class ArcherMutator(hyperparameters: ArcherHyperparameters) :
    PopulationMutator<Archer, EvaluatedArcher, ArcherHyperparameters>(hyperparameters) {

    override fun selectPopulationToBeRecombined(evaluatedPopulation: EvaluatedPopulation<Archer, EvaluatedArcher>): EvaluatedPopulation<Archer, EvaluatedArcher> {
        return evaluatedPopulation
    }

    override fun mutateEntity(entity: Archer): Archer {
        val dVelocity = Random.nextInt(-10, 10)
        val dAngle = Random.nextInt(-5, 5)

        return Archer(entity.velocity + dVelocity, entity.angle + dAngle)
    }
}