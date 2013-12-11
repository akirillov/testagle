package io.testagle.core

object TestHelpers {

  def readBytesFromClasspathResource(filename: String) = {
    val source = scala.io.Source.fromURL(getClass.getResource("/testagle-example.jar"))(scala.io.Codec.ISO8859)
    val byteArray = source.map(_.toByte).toArray
    source.close()

    byteArray
  }

}
