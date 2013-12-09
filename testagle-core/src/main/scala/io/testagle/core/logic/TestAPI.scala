package io.testagle.core.logic

import io.testagle.api.LoadTest

/**
 *
 * */
trait TestAPI {

  def loadTest(name: String, testImpl: LoadTest)

  def unloadTest(name: String)

  def getTest(name: String)

}
