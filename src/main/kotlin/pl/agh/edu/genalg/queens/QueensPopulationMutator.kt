package pl.agh.edu.genalg.queens

import pl.agh.edu.genalg.framework.flow.PopulationMutator
import pl.agh.edu.genalg.framework.metrics.Reporter

class QueensPopulationMutator(hyperparameters: QueensHyperparameters, reporter: Reporter) :
    PopulationMutator<Queens, EvaluatedQueens, QueensHyperparameters>(hyperparameters, reporter) {

    override fun mutateEntity(entity: Queens): Queens {
        val newPositions = entity.positions.toMutableSet()

        entity.positions
            .takeRandom(hyperparameters.maxNumberOfQueensToMutate)
            .forEach { p ->
                newPositions -= p
                val newPosition = generateSequence { Position.getRandom() }
                    .first { !newPositions.contains(it) }
                newPositions += newPosition
            }

        return Queens(newPositions)
    }
}