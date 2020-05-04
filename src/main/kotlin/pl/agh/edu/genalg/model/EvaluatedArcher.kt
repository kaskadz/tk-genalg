package pl.agh.edu.genalg.model

import pl.agh.edu.genalg.framework.EvaluatedEntity

class EvaluatedArcher(
    entity: Archer,
    val error: Double
) : EvaluatedEntity<Archer>(entity) {
    override fun toString(): String {
        return "EvaluatedArcher(error=$error, archer=$entity)"
    }
}