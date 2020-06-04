package pl.agh.edu.genalg.framework.metrics

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MetricsActor(
    private val coroutineScope: CoroutineScope
) {
    val metricsChannel: Channel<MetricLike> = Channel(Channel.UNLIMITED)
    private val metrics = mutableListOf<Metric>()

    fun start() = coroutineScope.launch {
        for (metric in metricsChannel) {
            when (metric) {
                is FacilityLog -> println("[${metric.origin}] ${metric.message}")
                is IslandLog -> println("[${metric.islandId}] #${metric.iteration} ${metric.message}")
                is Metric -> metrics.add(metric)
            }
        }
    }

    fun saveReport() {
        val iterationColumnName = Metric::iteration.name
        val islandIdColumnName = Metric::islandId.name

        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd_hh-mm-ss")
        val fileName = "metrics_${formatter.format(currentDateTime)}.csv"

        val rows = metrics.groupBy { Pair(it.islandId, it.iteration) }
            .map {
                val row = mutableMapOf<String, Any?>()
                row[islandIdColumnName] = it.key.first
                row[iterationColumnName] = it.key.second

                for (kv in it.value) {
                    row[kv.key] = kv.value
                }

                row
            }

        val headers = rows.flatMap { it.keys }.distinct()

        val rowsWithNa = rows.map {
            for (h in headers) {
                it.putIfAbsent(h, "N/A")
            }

            it
        }

        val rowLists = rowsWithNa.map { r ->
            headers.map { h -> r[h] }
        }

        csvWriter().open(fileName) {
            writeRow(headers)
            writeRows(rowLists)
        }
    }

}