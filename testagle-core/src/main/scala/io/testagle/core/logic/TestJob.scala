package io.testagle.core.logic

import io.testagle.api.LoadTest
import io.testagle.core.stats.RequestStats

class TestJob {
  def executeTest(test: LoadTest) = {
    val s = System.currentTimeMillis

    try{
      test.execute()
    } catch {
      case t: Throwable => RequestStats(System.currentTimeMillis - s, inError = true)
    }

    RequestStats(System.currentTimeMillis - s)
  }
}
