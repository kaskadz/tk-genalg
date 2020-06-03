package pl.agh.edu.genalg.framework

import kotlinx.coroutines.channels.SendChannel

interface Reporter {
    suspend fun log(message: String)
    suspend fun metric(key: String, value: Any)
}

class ReportContext(val islandId: Int, val iterationCountProvider: () -> Int)

class ContextReporter(
    private val reportContext: ReportContext,
    private val metricsChannel: SendChannel<MetricLike>
) : Reporter {

    override suspend fun log(message: String) {
        metricsChannel.send(Log(reportContext.iterationCountProvider(), reportContext.islandId, message))
    }

    override suspend fun metric(key: String, value: Any) {
        metricsChannel.send(Metric(reportContext.iterationCountProvider(), reportContext.islandId, key, value))
    }

}