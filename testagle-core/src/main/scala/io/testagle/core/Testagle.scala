package io.testagle.core

import java.net.{InetSocketAddress, SocketAddress}
import com.twitter.finagle.Service
import com.twitter.util.Future
import io.testagle.core.TestagleProtocol._
import java.lang.String
import com.twitter.finagle.builder.{Server, ClientBuilder, ServerBuilder}
import io.testagle.util.ProtobufCodec

/**
 * Core Service stereotype for dispatching load
 */
class Testagle extends Service[TestagleProtocol, TestagleProtocol] {
  def apply(request: TestagleProtocol) = {
    request match {
      case p: TestagleProtocol =>
        p.`type` match {
          case MessageType.LOAD_DESCRIPTION => {
            val loadDescription = p.`loadDescription`.get
            Future.value(TestagleProtocol(MessageType.LOAD_DESCRIPTION, None, None, Option(Ok("Everything OK")), None))  //TODO: provide proper handler
          }
          case _ => Future.value(TestagleProtocol(MessageType.LOAD_DESCRIPTION, None, None, None, Option(Error("Unappropriate message provided!"))))
        }
    }
  }
}

/**
 * Builder object for Testing Node (node that runs tests)
 */
object TestNode{
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
 * Builder object for Testing Server - central component that dispatches load tests to the clients
 */
object TestServer{
  val codec = ProtobufCodec(TestagleProtocol.getDefaultInstance)

  def apply(addresses: List[InetSocketAddress]): Service[TestagleProtocol, TestagleProtocol] =
    ClientBuilder()
      .codec(codec)
      .hosts(addresses)
      .retries(3)
      .hostConnectionLimit(5)
      .build()
}
