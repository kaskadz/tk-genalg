package pl.agh.edu.genalg.framework.model

class SemiEvaluatedPopulation<E : Entity, F : EvaluatedEntity<E>>(
    val entities: Collection<E>,
    val evaluatedEntities: Collection<F>
) : PopulationLike {
    override val size: Int
        get() = entities.size + evaluatedEntities.size
}