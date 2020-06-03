package pl.agh.edu.genalg.framework

sealed class MetricLike
data class Log(val iteration: Int, val islandId: Int, val message: String): MetricLike()
data class Metric(val iteration: Int, val islandId: Int, val key: String, val value: Any) : MetricLike()