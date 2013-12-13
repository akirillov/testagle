package io.testagle.core.stats

import io.testagle.core.TestagleProtocol.LoadStats

case class RequestStats (responseTime: Float, inError: Boolean = false)

case class TotalStats(completed: Int, total: Int, inError: Int, minLatency: Float, avgLatency: Float, meanLatency: Float, latency95: Float)


object TotalStats{

  def apply(stats: LoadStats): TotalStats = TotalStats(stats.`completedRequests`, stats.`totalRequests`, stats.`errors`, stats.`minLatency`, stats.`avgLatency`, stats.`meanLatency`, stats.`latency95`)


  def apply(stats: Seq[RequestStats]): TotalStats = {



     TotalStats(0,0,0,0f,0f,0f,0f)      //TODO: implement with list comprehension
  }
}


