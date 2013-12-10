package io.testagle.core.client

import io.testagle.core.{TestagleProtocol, TestagleClient, TestagleAPI}
import java.net.InetSocketAddress
import io.testagle.core.TestagleProtocol.{RunTest, Unload, LoadDescription}
import com.twitter.util.Future
import TestagleProtocol.MessageType._

/**
 *
 * TODO: document this ASAP!!!
 *
 */
class TestagleAPIClientImpl(addresses: List[InetSocketAddress]) extends TestagleAPI{

  val client = TestagleClient(addresses)

  def loadTest(container: LoadDescription) = {
    val responseFuture: Future[TestagleProtocol] = client(TestagleProtocol(LOAD_DESCRIPTION, Some(container)))

    handleResponse(responseFuture)
  }

  def unloadTest(testId: String) = {
    val responseFuture: Future[TestagleProtocol] = client(TestagleProtocol(UNLOAD_COMMAND, None, None, Some(Unload(testId))))

    handleResponse(responseFuture)
  }

  def runTest(testId: String) = {
    val responseFuture: Future[TestagleProtocol] = client(TestagleProtocol(UNLOAD_COMMAND, None, None, None, Some(RunTest(testId))))

    handleResponse(responseFuture)
  }

  def getTest(name: String) = ???   //unimplemented

  def handleResponse(responseFuture: Future[TestagleProtocol]) = responseFuture onSuccess
    { response => response.`type` match {
          case LOAD_STATS => println(response)
          case OK => response.`ok`.getOrElse(throw new Exception("Unrecoverable ERROR on target server!")).`testID`
          case ERROR => throw new Exception(response.`error`.get.`text`)
          case _ => println()
      }
    } onFailure {exception => throw new Exception("error", exception)}
}
