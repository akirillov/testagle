package io.testagle.example

import io.testagle.api.LoadTest

/**
 *  This test is used for testing testagle-core itself. Packed in jar class serves as naive implementation of load test.
 */
class TestagleTest extends LoadTest{
  def execute(){
    Thread.sleep(1000)
  }
}
