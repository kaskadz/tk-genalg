package pl.agh.edu.genalg.framework

abstract class SingleEntityEvaluator<E : Entity, F : EvaluatedEntity<E>, H : Hyperparameters>(hyperparameters: H) :
    PopulationEvaluator<E, F, H>(hyperparameters) {

    override fun evaluatePopulation(population: Population<E>): Collection<F> {
        return population.entities.map { e ->
            evaluateEntity(e)
        }.toList()
    }

    abstract fun evaluateEntity(entity: E): F
}