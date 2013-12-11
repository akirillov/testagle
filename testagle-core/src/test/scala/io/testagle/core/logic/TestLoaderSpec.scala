package io.testagle.core.logic

import io.testagle.core.TestHelpers._
import org.specs2.mutable.Specification


class TestLoaderSpec extends Specification {

  "Test Loader" should {

    "read data from bytes to file" in {
      val loader = new TestLoader

      val byteArray = readBytesFromClasspathResource("/testagle-example.jar")

      val uri = loader.writeFile(byteArray, "/tmp/testJar.jar")

      uri mustNotEqual null
      uri.toString.endsWith("/tmp/testJar.jar") mustEqual true
    }
  }
  "load and return new instance of LoadTest implementation" in {
    val loader = new TestLoader
    val byteArray = readBytesFromClasspathResource("/testagle-example.jar")

    val testClass = loader.uploadTestJarFromBytes(byteArray, "io.testagle.example.SampleHttpTest")

    println(testClass)

    testClass mustNotEqual null
    testClass.toString().contains("SampleHttpTest") mustEqual true

  }
}
