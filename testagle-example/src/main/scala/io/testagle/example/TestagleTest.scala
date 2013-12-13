package io.testagle.example

import io.testagle.api.LoadTest

/**
 *  This test is used for testing testagle-core itself. Packed in jar class serves as naive implementation of load test.
 */
class TestagleTest extends LoadTest{
  def execute(){
    def factorial(n: BigInt): BigInt = n match {
      case x if x < 1 => 1
      case x => x * factorial(n-1)
    }

    factorial(6000)
  }
}
