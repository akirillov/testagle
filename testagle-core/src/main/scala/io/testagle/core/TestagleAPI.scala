package io.testagle.core

import io.testagle.core.TestagleProtocol.LoadDescription

/**
 *
 * */
trait TestagleAPI {

  def loadTest(container: LoadDescription)

  def unloadTest(testId: String)

  def runTest(testId: String)
}
