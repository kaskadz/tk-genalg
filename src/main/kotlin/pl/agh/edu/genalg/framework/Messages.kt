package pl.agh.edu.genalg.framework

import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity

data class MigrationMessage<E : Entity>(val senderId: Int, val migrants: Collection<E>)

interface Results<E : Entity, F : EvaluatedEntity<E>> {
    val results: Collection<F>
}

sealed class ResultsMessage<E : Entity, F : EvaluatedEntity<E>> : Results<E, F>
data class FinishedResultsMessage<E : Entity, F : EvaluatedEntity<E>>(override val results: Collection<F>) :
    ResultsMessage<E, F>()

data class FinishedAndStopAllResultsMessage<E : Entity, F : EvaluatedEntity<E>>(override val results: Collection<F>) :
    ResultsMessage<E, F>()