package io.testagle.core.logic

import org.specs2.mutable.Specification
import io.testagle.core.TestHelpers._
import io.testagle.core.TestagleProtocol.{Ok, LoadDescription}
import com.google.protobuf.ByteString

class ServerAPIImplTest extends Specification{

  "Server API Implementation" should {
    "upload test and generate UUID" in {
      val byteArray = readBytesFromClasspathResource("/testagle-example.jar")
      val msg = LoadDescription("127.0.0.1", 5, 40, ByteString.copyFrom(byteArray), "io.testagle.example.TestagleTest")

      val testID = new TestagleAPIServerImplementation().loadTest(msg)

      testID must_!= null
      testID.length must_!= 0
    }
    "remove test by UUID" in {
      val byteArray = readBytesFromClasspathResource("/testagle-example.jar")
      val msg = LoadDescription("127.0.0.1", 5, 40, ByteString.copyFrom(byteArray), "io.testagle.example.TestagleTest")

      val server = new TestagleAPIServerImplementation()

      val testID = server.loadTest(msg)

      server.unloadTest(testID) mustEqual testID
      server.unloadTest(testID) mustEqual null
    }
  }

  "Server API Implementation" should {
    "run specified test at specified concurrency" in {
      val byteArray = readBytesFromClasspathResource("/testagle-example.jar")
      val msg = LoadDescription("127.0.0.1", 5, 40, ByteString.copyFrom(byteArray), "io.testagle.example.TestagleTest")

      val server = new TestagleAPIServerImplementation()

      val testID = server.loadTest(msg)

      val testResult = server.runTest(testID)

      println(testResult)


      testID must_!= null
      testID.length must_!= 0

      server.unloadTest(testID) mustEqual testID
      server.unloadTest(testID) mustEqual null


    }
  }
}
