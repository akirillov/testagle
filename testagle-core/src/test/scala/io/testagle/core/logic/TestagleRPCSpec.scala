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

      val server = TestagleServer(8383, "test node")
      val client = TestagleClient(List(new InetSocketAddress("127.0.0.1", 8383)))

      val byteArray = readBytesFromClasspathResource("/testagle-example.jar")

      val msg = LoadDescription("127.0.0.1", 4, 1000, ByteString.copyFrom(byteArray), "io.testagle.example.TestagleTest", 1000)

      val testResponse = client(TestagleProtocol(LOAD_DESCRIPTION, Some(msg)))

      val response = Await.result(testResponse)

      client.close()
      server.close()

      while server

      response.`ok` mustNotEqual None

      val testID = response.`ok`.get.`testID`

      testID must_!= null
      testID.length must_!= 0
    }

    "properly dispatch unload request by ID" in {

      val server = TestagleServer(8383, "test node")
      val client = TestagleClient(List(new InetSocketAddress("127.0.0.1", 8383)))

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

    "generate error in case of UUID abscense" in {
      val server = TestagleServer(8383, "test node")
      val client = TestagleClient(List(new InetSocketAddress("127.0.0.1", 8383)))

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


  /*

   "Server API Implementation" should {
    "upload test and generate UUID" in {
      val byteArray = readBytesFromClasspathResource("/testagle-example.jar")
      val msg = LoadDescription("127.0.0.1", 5, 40, ByteString.copyFrom(byteArray), "io.testagle.example.TestagleTest", 10)

      val testID = new TestagleAPIServerImplementation().loadTest(msg)

      testID must_!= null
      testID.length must_!= 0
    }
    "remove test by UUID" in {
      val byteArray = readBytesFromClasspathResource("/testagle-example.jar")
      val msg = LoadDescription("127.0.0.1", 5, 40, ByteString.copyFrom(byteArray), "io.testagle.example.TestagleTest", 10)

      val server = new TestagleAPIServerImplementation()

      val testID = server.loadTest(msg)

      server.unloadTest(testID) mustEqual testID
      server.unloadTest(testID) mustEqual null
    }
  }

  "Server API Implementation" should {
    "run specified test at specified concurrency" in {
      val byteArray = readBytesFromClasspathResource("/testagle-example.jar")
      val msg = LoadDescription("127.0.0.1", 4, 1000, ByteString.copyFrom(byteArray), "io.testagle.example.TestagleTest", 100)

      val server = new TestagleAPIServerImplementation()

      val testID = server.loadTest(msg)

      val testResult = server.runTest(testID)

      testResult must_!= null

      server.unloadTest(testID) mustEqual testID
      server.unloadTest(testID) mustEqual null
    }
  }

  */
}
