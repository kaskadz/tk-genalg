package pl.agh.edu.genalg.framework.model

class Population<E : Entity>(val entities: Collection<E> = emptyList()) : PopulationLike<E> {
    override val size: Int
        get() = entities.size
    override val allEntities: Collection<E>
        get() = entities
}