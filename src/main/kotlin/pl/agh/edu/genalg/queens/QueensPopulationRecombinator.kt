package pl.agh.edu.genalg.queens

import pl.agh.edu.genalg.framework.flow.CouplePopulationRecombinator
import pl.agh.edu.genalg.framework.metrics.Reporter
import pl.agh.edu.genalg.framework.model.EvaluatedPopulation
import pl.agh.edu.genalg.framework.model.Population
import kotlin.math.roundToInt
import kotlin.random.Random

class QueensPopulationRecombinator(hyperparameters: QueensHyperparameters, reporter: Reporter) :
    CouplePopulationRecombinator<Queens, EvaluatedQueens, QueensHyperparameters>(hyperparameters, reporter) {

    override fun recombineCouple(entity1: Queens, entity2: Queens): Queens {
        val cutPoint = Random.nextInt(0, hyperparameters.boardSize)

        val part1 = entity1.positions.toTypedArray().sliceArray(0 until cutPoint)
        val part2 = entity2.positions.toTypedArray().sliceArray(cutPoint until hyperparameters.boardSize)

        val newPositions = (part1 + part2).toMutableSet()

        while (newPositions.size < hyperparameters.boardSize) {
            val newPosition = generateSequence { Position.getRandom(hyperparameters.boardSize) }
                .first { !newPositions.contains(it) }

            newPositions += newPosition
        }

        return Queens(newPositions, hyperparameters.boardSize)
    }

    override fun selectPopulationToBeRecombined(evaluatedPopulation: EvaluatedPopulation<Queens, EvaluatedQueens>): Population<Queens> {
        val numberOfEntitiesChosenToRecombine =
            (evaluatedPopulation.size * 2 * hyperparameters.reproductionRate).roundToInt()

        val entitiesChosenToRecombine = evaluatedPopulation.evaluatedEntities
            .sortedBy { it.fitness }
            .take(numberOfEntitiesChosenToRecombine)
            .map { it.entity }

        return Population(entitiesChosenToRecombine)
    }
}