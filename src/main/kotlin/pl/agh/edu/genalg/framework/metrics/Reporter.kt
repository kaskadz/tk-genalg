package pl.agh.edu.genalg.framework.metrics

import kotlinx.coroutines.channels.SendChannel

interface Reporter {
    suspend fun log(message: String)
    suspend fun metric(key: String, value: Any?)
}

class IslandReportContext(val islandId: Int, val iterationCountProvider: () -> Int)

class IslandContextReporter(
    private val reportContext: IslandReportContext,
    private val metricsChannel: SendChannel<MetricLike>
) : Reporter {

    override suspend fun log(message: String) {
        metricsChannel.send(
            IslandLog(
                reportContext.iterationCountProvider(),
                reportContext.islandId,
                message
            )
        )
    }

    override suspend fun metric(key: String, value: Any?) {
        metricsChannel.send(
            Metric(
                reportContext.iterationCountProvider(),
                reportContext.islandId,
                key,
                value
            )
        )
    }
}

class FacilityContextReporter(private val origin: String, private val metricsChannel: SendChannel<MetricLike>) :
    Reporter {

    override suspend fun log(message: String) {
        metricsChannel.send(
            FacilityLog(origin, message)
        )
    }

    override suspend fun metric(key: String, value: Any?) {
        metricsChannel.send(
            Metric(-1, -1, key, value)
        )
    }
}