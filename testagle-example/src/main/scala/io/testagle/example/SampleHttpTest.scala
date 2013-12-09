package io.testagle.example

import io.testagle.api.LoadTest
import org.jboss.netty.handler.codec.http._
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.Service
import com.twitter.util.Future
import com.twitter.finagle.http.Http
import org.jboss.netty.handler.codec.http.HttpVersion._
import org.jboss.netty.handler.codec.http.HttpMethod._

class SampleHttpTest extends LoadTest{

  //Idea behind: single test executed according various load descriptors running single test scenario implemented inside
  //execute method
  def execute(){
    val client: Service[HttpRequest, HttpResponse] = ClientBuilder()
      .codec(Http())
      .hosts("twitter.com:80")
      .hostConnectionLimit(1)
      .build()

    // Issue a request, get a response:
    val request: HttpRequest = new DefaultHttpRequest(HTTP_1_1, GET, "/")
    val responseFuture: Future[HttpResponse] = client(request)

    responseFuture onSuccess { response => println("Received response: " + response) } onFailure {exception => println("failed: ", exception)}
  }
}