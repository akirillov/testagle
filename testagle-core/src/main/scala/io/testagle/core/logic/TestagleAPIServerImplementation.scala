package io.testagle.core.logic

import io.testagle.api.LoadTest
import io.testagle.core.TestagleProtocol.{LoadDescription}
import java.util.concurrent.ConcurrentHashMap
import io.testagle.core.{TestagleAPI}
import java.util.{UUID}
import io.testagle.core.stats.{RequestStats, TotalStats}
import scala.concurrent._
import scala.concurrent.duration._
import ExecutionContext.Implicits.global
import java.util

/**
 * Class that loads provided tests and runs 'em
 */
class TestagleAPIServerImplementation extends TestagleAPI{

  case class LoadDescriptor(concurrency: Int, totalRequests: Int, timeLimit: Int, test: LoadTest)
  private val tests = new ConcurrentHashMap[String, LoadDescriptor]()

  private def getLoadTest( jar: Array[Byte], className: String) = new TestLoader().uploadTestJarFromBytes(jar, className)

  def runTest(testId: String) = {
    if( ! tests.containsKey(testId) ) { throw new RuntimeException("No test with such ID!") }

    tests.contains("AFTER THROW()")

    val loadDescriptor = tests.get(testId)

    val executors: Seq[TestExecutor] = (1 to loadDescriptor.concurrency).map(_ => new TestExecutor())


    //TODO: implementation really sucks in idiomatic sense but it parallels good across the cores
    val results: Seq[Future[List[RequestStats]]] = executors.map(t => t.executeMultipleTests(loadDescriptor.test, loadDescriptor.totalRequests/loadDescriptor.concurrency))


    def awaitRes(in: Future[List[RequestStats]]): List[RequestStats] = Await.result(in, loadDescriptor.timeLimit seconds)

    val myFutureList = Future.traverse(results)(x => Future(awaitRes(x)))

     val awaited = Await.result(myFutureList, loadDescriptor.timeLimit seconds).flatten


    //TODO: implement await switch on load type (see roadmap)

    TotalStats(awaited)
  }

  def loadTest(container: LoadDescription) = {
    val test = getLoadTest(container.`data`.toByteArray, container.`testClass`)

    val testId = UUID.randomUUID().toString
    tests.put(testId, LoadDescriptor(container.`concurrency`, container.`totalRequests`, container.`timeLimitSeconds`, test))

    testId
  }

  def unloadTest(testId: String) = {
    tests.remove(testId) match {
      case descriptor: LoadDescriptor => testId
      case null => null
    }
  }

  //  private def generateError(msg: String) = TestagleProtocol(ERROR, None, None, None, None, None, Some(Error(msg)))

  //  private def generateOk(msg: String) = TestagleProtocol(OK, None, None, None, None, Some(Ok(msg)))
}


