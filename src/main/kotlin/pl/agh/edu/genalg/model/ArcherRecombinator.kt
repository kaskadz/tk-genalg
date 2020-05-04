package pl.agh.edu.genalg.model

import pl.agh.edu.genalg.framework.CouplePopulationRecombinator
import pl.agh.edu.genalg.framework.EvaluatedPopulation
import kotlin.random.Random

class ArcherRecombinator(hyperparameters: ArcherHyperparameters) :
    CouplePopulationRecombinator<Archer, EvaluatedArcher, ArcherHyperparameters>(hyperparameters) {

    override fun selectPopulationToBeRecombined(evaluatedPopulation: EvaluatedPopulation<Archer, EvaluatedArcher>): EvaluatedPopulation<Archer, EvaluatedArcher> {
        return evaluatedPopulation
    }

    override fun recombineCouple(entity1: Archer, entity2: Archer): Archer {
        return if (Random.nextInt() % 2 == 0) {
            val entities = arrayOf(entity1, entity2)
            val velocity = entities.map { it.velocity }.average().toInt() + Random.nextInt(-5, 5)
            val angle = entities.map { it.angle }.average().toInt() + Random.nextInt(-5, 5)

            Archer(velocity, angle)
        } else {
            if (Random.nextInt() % 2 == 0) {
                Archer(entity1.velocity, entity2.angle)
            } else {
                Archer(entity2.velocity, entity1.angle)
            }
        }
    }
}