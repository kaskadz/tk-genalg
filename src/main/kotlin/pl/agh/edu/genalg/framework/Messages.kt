package pl.agh.edu.genalg.framework

import pl.agh.edu.genalg.framework.model.Entity
import pl.agh.edu.genalg.framework.model.EvaluatedEntity

data class MigrationMessage<E : Entity>(val senderId: Int, val migrants: Collection<E>)
data class ResultsMessage<E: Entity, F: EvaluatedEntity<E>>(val results: Collection<F>)