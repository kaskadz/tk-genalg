package pl.agh.edu.genalg.framework.model

class EvaluatedPopulation<E : Entity, F : EvaluatedEntity<E>>(val evaluatedEntities: Collection<F>) : PopulationLike<E> {
    override val size: Int
        get() = evaluatedEntities.size
    override val allEntities: Collection<E>
        get() = evaluatedEntities.map { it.entity }
}