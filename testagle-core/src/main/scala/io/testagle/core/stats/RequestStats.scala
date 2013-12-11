package io.testagle.core.stats

case class RequestStats (responseTime: Float, inError: Boolean = false)
case class TotalStats(completed: Int, total: Int, inError: Int, minLatency: Float, avgLatency: Float, meanLatency: Float, latency95: Float)

object StatsAggregator{
  def aggregate(stats: List[RequestStats]): TotalStats = {
     TotalStats(0,0,0,0f,0f,0f,0f)      //TODO: remove
  }
}


