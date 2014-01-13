package io.testagle.core.logic

import org.specs2.mutable.Specification
import io.testagle.core.TestHelpers._
import io.testagle.core.TestagleProtocol._
import io.testagle.core.TestagleProtocol.MessageType._
import com.google.protobuf.ByteString
import io.testagle.core.{TestagleProtocol, TestagleClient, TestagleServer}
import java.net.InetSocketAddress
import com.twitter.util.Await

class TestagleRPCSpec extends Specification {

  "Testagle client-server RPC" should {
    "properly dispatch upload test request and UUID generation" in {

      val port = 8300
      val server = TestagleServer(port, "test node")
      val client = TestagleClient(List(new InetSocketAddress("127.0.0.1", port)))

      val byteArray = readBytesFromClasspathResource("/testagle-example.jar")

      val msg = LoadDescription("127.0.0.1", 4, 1000, ByteString.copyFrom(byteArray), "io.testagle.example.TestagleTest", 1000)

      val testResponse = client(TestagleProtocol(LOAD_DESCRIPTION, Some(msg)))

      val response = Await.result(testResponse)

      client.close()
      server.close()

      response.`ok` mustNotEqual None

      val testID = response.`ok`.get.`testID`

      testID must_!= null
      testID.length must_!= 0
    }

    "properly dispatch unload request by ID" in {

      val port = 8301
      val server = TestagleServer(port, "test node")
      val client = TestagleClient(List(new InetSocketAddress("127.0.0.1", port)))

      val byteArray = readBytesFromClasspathResource("/testagle-example.jar")

      val msg = LoadDescription("127.0.0.1", 4, 1000, ByteString.copyFrom(byteArray), "io.testagle.example.TestagleTest", 1000)

      val response = Await.result(client(TestagleProtocol(LOAD_DESCRIPTION, Some(msg))))

      response.`ok` mustNotEqual None

      val testID = response.`ok`.get.`testID`

      val unloadResponse = Await.result(client(TestagleProtocol(UNLOAD_COMMAND, None, None, Some(Unload(testID)))))

      client.close()
      server.close()

      unloadResponse.`ok` mustNotEqual None
      unloadResponse.`ok`.get.`testID` mustEqual testID
    }

    "generate error in case of UUID absence" in {

      val port = 8302
      val server = TestagleServer(port, "test node")
      val client = TestagleClient(List(new InetSocketAddress("127.0.0.1", port)))

      val byteArray = readBytesFromClasspathResource("/testagle-example.jar")

      val msg = LoadDescription("127.0.0.1", 4, 1000, ByteString.copyFrom(byteArray), "io.testagle.example.TestagleTest", 1000)

      val response = Await.result(client(TestagleProtocol(LOAD_DESCRIPTION, Some(msg))))

      response.`ok` mustNotEqual None

      val testID = response.`ok`.get.`testID`

      val unloadResponse = Await.result(client(TestagleProtocol(UNLOAD_COMMAND, None, None, Some(Unload(testID)))))

      unloadResponse.`ok` mustNotEqual None
      unloadResponse.`ok`.get.`testID` mustEqual testID

      val unloadError = Await.result(client(TestagleProtocol(UNLOAD_COMMAND, None, None, Some(Unload(testID)))))

      client.close()
      server.close()

      unloadError.`ok` mustEqual None
      unloadError.`error` mustNotEqual None
      unloadError.`error`.get.`text` mustNotEqual ""
    }
  }
}
