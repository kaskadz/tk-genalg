package pl.agh.edu.genalg.framework.model

class MigrationSelection<E : Entity>(
    val migrants: Collection<E>,
    val nonMigrants: Collection<E>
)
