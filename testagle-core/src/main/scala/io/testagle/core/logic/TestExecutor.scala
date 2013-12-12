package io.testagle.core.logic

import io.testagle.api.LoadTest
import io.testagle.core.stats.RequestStats
import scala.concurrent._
import ExecutionContext.Implicits.global

class TestExecutor {

  //sequential for now
  private def executeTest(test: LoadTest): RequestStats = {
      val s = System.currentTimeMillis
      try{
        test.execute()
        RequestStats(System.currentTimeMillis - s)
      } catch {
        case t: Throwable => RequestStats(System.currentTimeMillis - s, inError = true)
      }
  }

  def executeMultipleTests(test: LoadTest, amount: Int): Future[List[RequestStats]] = {
    future{
      (1 to amount).map(_ => executeTest(test)).toList
    }
  }
}
