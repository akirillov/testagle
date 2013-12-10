package io.testagle.core.logic

import io.testagle.api.LoadTest
import io.testagle.core.stats.RequestStats
import com.twitter.util.Future

class TestJob {
  def executeTest(test: LoadTest) = {
    val s = System.currentTimeMillis

    val result: Future[RequestStats] = {
      try{
        test.execute()
      } catch {
        case t: Throwable => Future(RequestStats(System.currentTimeMillis - s, inError = true))
      }

      Future(RequestStats(System.currentTimeMillis - s))
    }

    result
  }
}
