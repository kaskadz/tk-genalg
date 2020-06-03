package pl.agh.edu.genalg.framework.model

class EvaluatedPopulation<E : Entity, F : EvaluatedEntity<E>>(val evaluatedEntities: Collection<F>) : PopulationLike {
    override val size: Int
        get() = evaluatedEntities.size
}