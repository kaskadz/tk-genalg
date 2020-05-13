package pl.agh.edu.genalg.framework

import pl.agh.edu.genalg.framework.model.Entity

data class MigrationMessage<E : Entity>(val migrants: Collection<E>)