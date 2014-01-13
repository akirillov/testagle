package io.testagle.core

import io.testagle.core.TestagleProtocol.LoadDescription
import io.testagle.core.stats.TotalStats

/**
 *
 * */
trait TestagleAPI {

  def loadTest(container: LoadDescription): String

  def unloadTest(testId: String): Boolean

  def runTest(testId: String): TotalStats
}
