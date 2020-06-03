package pl.agh.edu.genalg.framework.model

interface PopulationLike<E : Entity> {
    val size: Int
    val allEntities: Collection<E>
}