package io.testagle.core.logic

import io.testagle.api.LoadTest
import io.testagle.core.TestagleProtocol.LoadDescription
import java.util.concurrent.ConcurrentHashMap

/**
 * Class that loads provided tests and runs 'em
 */
class TestRunner extends TestAPI{

  private val tests = new ConcurrentHashMap[String, LoadTest]()

  private def getLoadTest( jar: Array[Byte], className: String) = new TestLoader().uploadTestJarFromBytes(jar, className)

  def initTest(testData: LoadDescription) = {
    getLoadTest(testData.`data`.toByteArray, testData.`testClass`)
  }

  def runTest(testID: String) = {

  }

  def loadTest(name: String, testImpl: LoadTest) = ???

  def unloadTest(name: String) = ???

  def getTest(name: String) = ???
}
