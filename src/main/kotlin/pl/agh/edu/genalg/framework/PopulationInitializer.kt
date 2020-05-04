package pl.agh.edu.genalg.framework

abstract class PopulationInitializer<E : Entity, H : Hyperparameters>(val hyperparameters: H) {
    abstract fun initializePopulation(): Population<E>
}