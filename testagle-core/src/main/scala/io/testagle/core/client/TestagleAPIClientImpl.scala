package io.testagle.core.client

import io.testagle.core.{TestagleProtocol, TestagleClient, TestagleAPI}
import java.net.InetSocketAddress
import io.testagle.core.TestagleProtocol.{RunTest, Unload, LoadDescription}
import com.twitter.util.{Await, Future}
import TestagleProtocol.MessageType._
import io.testagle.core.stats.TotalStats

/**
 *
 * Client implementation sends data to remote nodes and awaits results (blocking)
 *
 */
class TestagleAPIClientImpl(addresses: List[InetSocketAddress]) extends TestagleAPI{

  val client = TestagleClient(addresses)

  def loadTest(container: LoadDescription) = {
    val responseFuture: Future[TestagleProtocol] = client(TestagleProtocol(LOAD_DESCRIPTION, Some(container)))

    val response = Await.result(responseFuture)
    response.`type` match {
      case OK => response.`ok`.getOrElse(throw new Exception("Unrecoverable ERROR on target server!")).`testID`
    }
  }

  def unloadTest(testId: String) = {
    val responseFuture: Future[TestagleProtocol] = client(TestagleProtocol(UNLOAD_COMMAND, None, None, Some(Unload(testId))))

    val response = Await.result(responseFuture)
    //here we match response from our server (other side of API implementation) that guarantees ID presence in case of OK response
    response.`type` match {
      case OK => true
      case _ => false
    }
  }

  def runTest(testId: String) = {
    val responseFuture: Future[TestagleProtocol] = client(TestagleProtocol(UNLOAD_COMMAND, None, None, None, Some(RunTest(testId))))

    val response = Await.result(responseFuture)
    response.`type` match {
      case LOAD_STATS => TotalStats(response.`loadStats`.get)
    }
  }

  def throwError(t: Throwable){ throw new Exception("error", t) }
  def throwError(msg: String){ throw new Exception(msg) }

}
