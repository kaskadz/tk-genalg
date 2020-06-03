package pl.agh.edu.genalg.framework.model

class Population<E : Entity>(val entities: Collection<E> = emptyList()) : PopulationLike {
    override val size: Int
        get() = entities.size
}