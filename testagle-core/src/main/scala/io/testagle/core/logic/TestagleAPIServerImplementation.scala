package io.testagle.core.logic

import io.testagle.api.LoadTest
import io.testagle.core.TestagleProtocol.{LoadStats, Ok, Error, LoadDescription}
import java.util.concurrent.ConcurrentHashMap
import io.testagle.core.{TestagleProtocol, TestagleAPI}
import io.testagle.core.TestagleProtocol.MessageType._
import java.util.{UUID}
import io.testagle.core.stats.TotalStats
import com.twitter.util.Future

/**
 * Class that loads provided tests and runs 'em
 */
class TestagleAPIServerImplementation extends TestagleAPI{

  case class LoadDescriptor(concurrency: Int, totalRequests: Int, test: LoadTest)
  private val tests = new ConcurrentHashMap[String, LoadDescriptor]()


  private def getLoadTest( jar: Array[Byte], className: String) = new TestLoader().uploadTestJarFromBytes(jar, className)

  implicit def Stats2Protobuf(value : TotalStats) = TestagleProtocol(LOAD_STATS, None, Some(LoadStats(value.)))

  def runTest(testId: String) = {
    if(!tests.contains(testId)) generateError("No test with such ID!")

    val loadDescriptor = tests.get(testId)

    val testJobs = (1 to loadDescriptor.concurrency).map(_ => new TestJob())

    val allTheSquares: Future[List[Int]] = scala.concurrent.Future.traverse(numberList)




    val future1 = Future(timeTakingIdentityFunction(1))
    val future2 = Future(timeTakingIdentityFunction(2))
    val future3 = Future(timeTakingIdentityFunction(3))

    val future = for {
      x <- future1
      y <- future2
      z <- future3
    } yield (x + y + z)

    future onSuccess {
      case sum =>
        val elapsedTime = ((System.currentTimeMillis - startTime) / 1000.0)
        println("Sum of 1, 2 and 3 is " + sum + " calculated in " + elapsedTime + " seconds")
    }

//    1 to loadDescriptor.concurrency

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

  def generateError(msg: String) = TestagleProtocol(ERROR, None, None, None, None, None, Some(Error(msg)))

  def generateOk(msg: String) = TestagleProtocol(OK, None, None, None, None, Some(Ok(msg)))
}


