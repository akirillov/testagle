package io.testagle.core.logic

import org.specs2.mutable.Specification
import io.testagle.core.TestHelpers._
import io.testagle.core.TestagleProtocol.{Ok, LoadDescription}
import com.google.protobuf.ByteString

class ServerAPIImplTest extends Specification{

  "Server API Implementation" should {

    "load test and generate UUID" in {
      val byteArray = readBytesFromClasspathResource("/testagle-example.jar")
      val msg = LoadDescription("127.0.0.1", 5, 40, ByteString.copyFrom(byteArray), "io.testagle.example.test.SampleHttpTest")

      val resp = new TestagleAPIServerImplementation().loadTest(msg).asInstanceOf[Ok]

      println(resp)

      resp mustNotEqual null
      resp.`testID`.isEmpty mustEqual false
    }
    "load load test and remove it by UUID" in {
      val byteArray = readBytesFromClasspathResource("/testagle-example.jar")
      val msg = LoadDescription("127.0.0.1", 5, 40, ByteString.copyFrom(byteArray), "io.testagle.example.test.SampleHttpTest")

      val server = new TestagleAPIServerImplementation()

      val resp = server.loadTest(msg).asInstanceOf[Ok]

      val id = resp.`testID`
      id.isEmpty mustEqual false

      server.unloadTest(id) mustEqual id
    }
  }
}
