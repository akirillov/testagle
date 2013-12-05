package io.testagle.core.logic

import io.testagle.api.LoadTest
import io.testagle.core.TestagleProtocol.LoadDescription

/**
 * Class that runs provided tests
 *
 */
class TestRunner { //TODO: provide scaldi wiring

  private def getLoadTest( jar: Array[Byte]): LoadTest = {
    //http://stackoverflow.com/questions/8867766/scala-dynamic-object-class-loading


    //TODO: actually there are a lot of ways to dynamically load jar in runtime
    //some things to keep in mind:
    // 1) jar will be a large one - with all its dependencies (like protobuf generated code) and so on
    // 2) provide class name in message vs. full scan of jar's interface implementations
    null
  }

  def initTest(testData: LoadDescription) = {
    getLoadTest(testData.`data`.toByteArray)
  }

  def runTest(testID: LoadTest) = {

  }

}
