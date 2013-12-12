package io.testagle.example.test

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.http.Http
import org.jboss.netty.handler.codec.http._
import com.twitter.finagle.{Service}
import com.twitter.util.Future
import io.testagle.api.LoadTest

class SampleHttpTest extends LoadTest{
  def execute(){
    val client: Service[HttpRequest, HttpResponse] = ClientBuilder()
      .codec(Http())
      .hosts("twitter.com:80")
      .hostConnectionLimit(1)
      .build()

    val request: HttpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/")
    val responseFuture: Future[HttpResponse] = client(request)

    responseFuture onSuccess { response => println("Received response: " + response) } onFailure {exception => println("failed: ", exception)}
  }
}