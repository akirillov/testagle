package io.testagle.core

import java.net.{InetSocketAddress, SocketAddress}
import com.twitter.finagle.Service
import com.twitter.util.Future
import io.testagle.core.TestagleProtocol._
import io.testagle.core.TestagleProtocol.MessageType._
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
          case LOAD_DESCRIPTION => {
            val loadDescription = p.`loadDescription`.get
            Future.value(TestagleProtocol(OK, None, None, None, None, Some(Ok("testID"))))
          }
          case _ => Future.value(TestagleProtocol(ERROR, None, None, None, None, None, Some(Error("Unappropriate message provided!"))))
        }
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
 * Builder object for Testing Server - central component that dispatches load tests to the clients
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
