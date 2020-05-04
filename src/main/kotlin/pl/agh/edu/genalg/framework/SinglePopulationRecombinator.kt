package pl.agh.edu.genalg.framework

abstract class SinglePopulationRecombinator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(hyperparameters: H) :
    PopulationRecombinator<E, F, H>(hyperparameters) {

    override fun recombinePopulation(evaluatedPopulation: EvaluatedPopulation<E, F>): Population<E> {
        val newEntities = evaluatedPopulation.evaluatedEntities
            .map { it.entity }
            .map { recombineSingle(it) }

        return Population(newEntities)
    }

    abstract fun recombineSingle(entity: E): E
}