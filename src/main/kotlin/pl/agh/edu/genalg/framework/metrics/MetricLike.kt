package pl.agh.edu.genalg.framework.metrics

sealed class MetricLike
data class FacilityLog(val origin: String, val message: String): MetricLike()
data class IslandLog(val iteration: Int, val islandId: Int, val message: String): MetricLike()
data class Metric(val iteration: Int, val islandId: Int, val key: String, val value: Any?) : MetricLike()