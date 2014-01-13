package io.testagle.core

import java.net.{InetSocketAddress, SocketAddress}
import com.twitter.finagle.Service
import com.twitter.util.Future
import io.testagle.core.TestagleProtocol._
import io.testagle.core.TestagleProtocol.MessageType._
import java.lang.String
import com.twitter.finagle.builder.{Server, ClientBuilder, ServerBuilder}
import io.testagle.util.ProtobufCodec
import scala.Predef
import io.testagle.core.logic.TestagleAPIServerImplementation
import io.testagle.core.stats.TotalStats

/**
 * Core Service stereotype for dispatching load
 */
class Testagle extends Service[TestagleProtocol, TestagleProtocol] {

  val core: TestagleAPI = new TestagleAPIServerImplementation

  /*
  required string nodeName = 1;
     required int32 completedRequests = 2;
     required int32 totalRequests = 3;
     required int32 errors = 4;
     required float minLatency = 5;
     required float avgLatency = 6;
     required float meanLatency = 7;
     required float latency95 = 8;
 */
  implicit def toLoadStats(stats: TotalStats) = LoadStats("NODE NAME", stats.completed, stats.total, stats.inError, stats.minLatency, stats.avgLatency, stats.meanLatency, stats.latency95)

  def apply(request: TestagleProtocol) = {
    try{
    request match {
      case p: TestagleProtocol =>
        p.`type` match {
          case LOAD_DESCRIPTION => Future.value(TestagleProtocol(OK, None, None, None, None, Some(Ok(core.loadTest(p.`loadDescription`.get)))))

          case UNLOAD_COMMAND => if(core.unloadTest(p.`unload`.get.`testID`)) {
            Future.value(TestagleProtocol(OK, None, None, None, None, Some(Ok(p.`unload`.get.`testID`))))
          } else {
            Future.value(TestagleProtocol(ERROR, None, None, None, None, None, Some(Error("No tests with specified ID found!"))))
          }

          case RUNTEST_COMMAND => Future.value(TestagleProtocol(LOAD_STATS, None, Some(core.runTest(p.`runTest`.get.`testID`)), None, None, None))
          case _ => Future.value(TestagleProtocol(ERROR, None, None, None, None, None, Some(Error("Unappropriate message provided!"))))
        }
    }
    } catch {
      case t: Throwable => Future.value(TestagleProtocol(ERROR, None, None, None, None, None, Some(Error(t.getMessage))))
    }
  }
}

/**
 * Builder object for Testing Node (node that runs tests)
 */
object TestagleServer{
  val codec = ProtobufCodec(TestagleProtocol.getDefaultInstance)

  def apply(port: Int, name: String) = {
    val address: SocketAddress = new InetSocketAddress(port)

    val server: Server = ServerBuilder()
      .codec(codec)
      .bindTo(address)
      .name(name+"@"+port)
      .build(new Testagle)

    server
  }
}

/**
 * Builder object for Testing Server - central component that dispatches load tests to the nodes
 */
object TestagleClient{
  val codec = ProtobufCodec(TestagleProtocol.getDefaultInstance)

  def apply(addresses: List[InetSocketAddress]): Service[TestagleProtocol, TestagleProtocol] =
    ClientBuilder()
      .codec(codec)
      .hosts(addresses)
      .retries(3)
      .hostConnectionLimit(5)
      .build()
}
