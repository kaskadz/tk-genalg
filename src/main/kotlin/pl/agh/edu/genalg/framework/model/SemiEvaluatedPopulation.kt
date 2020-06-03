package pl.agh.edu.genalg.framework.model

class SemiEvaluatedPopulation<E : Entity, F : EvaluatedEntity<E>>(
    val entities: Collection<E>,
    val evaluatedEntities: Collection<F>
) : PopulationLike<E> {
    override val size: Int
        get() = entities.size + evaluatedEntities.size
    override val allEntities: Collection<E>
        get() = evaluatedEntities.map { it.entity } + entities
}