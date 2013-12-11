package io.testagle.core.logic

import io.testagle.api.LoadTest
import io.testagle.core.TestagleProtocol.{LoadStats, Ok, Error, LoadDescription}
import java.util.concurrent.ConcurrentHashMap
import io.testagle.core.{TestagleProtocol, TestagleAPI}
import io.testagle.core.TestagleProtocol.MessageType._
import java.util.{UUID}
import io.testagle.core.stats.{RequestStats, TotalStats}
import scala.concurrent._
import ExecutionContext.Implicits.global

/**
 * Class that loads provided tests and runs 'em
 */
class TestagleAPIServerImplementation extends TestagleAPI{

  case class LoadDescriptor(concurrency: Int, totalRequests: Int, test: LoadTest)
  private val tests = new ConcurrentHashMap[String, LoadDescriptor]()


  private def getLoadTest( jar: Array[Byte], className: String) = new TestLoader().uploadTestJarFromBytes(jar, className)

  //implicit def Stats2Protobuf(value : TotalStats) = TestagleProtocol(LOAD_STATS, None, Some(LoadStats(value.)))

  def runTest(testId: String) = {
    if(!tests.contains(testId)) generateError("No test with such ID!")

    val loadDescriptor = tests.get(testId)

    val executors: Seq[TestExecutor] = (1 to loadDescriptor.concurrency).map(_ => new TestExecutor())

    val results = future {
      executors.map(t => t.executeMultipleTests(loadDescriptor.test, loadDescriptor.totalRequests/loadDescriptor.concurrency))
    }



    //TODO : parallel execution will be implemented in feature branch
  }

  def loadTest(container: LoadDescription) = {
    val test = getLoadTest(container.`data`.toByteArray, container.`testClass`)
    //TODO: pool warm up etc goes here (blocking, have to be thread-safe)
    val testId = UUID.randomUUID().toString
    tests.put(testId, LoadDescriptor(container.`concurrency`, container.`totalRequests`, test))

    generateOk(testId)
  }

  def unloadTest(testId: String) = {
    tests.remove(testId) match {
      case descriptor: LoadDescriptor => generateOk(testId)
      case _ => generateError("No test found with ID "+testId)
    }
  }

  private def generateError(msg: String) = TestagleProtocol(ERROR, None, None, None, None, None, Some(Error(msg)))

  private def generateOk(msg: String) = TestagleProtocol(OK, None, None, None, None, Some(Ok(msg)))
}


