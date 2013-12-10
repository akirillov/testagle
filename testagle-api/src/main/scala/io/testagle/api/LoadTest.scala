package io.testagle.api

/**
 *
 * Main API trait.
 *
 * Implementations of this trait will be accepted and executed by test runner in Testagle core component
 *
 */
trait LoadTest {

  def execute()

  /*/**
   * Implementation should contain warm up activities such as connection pool allocation and initialization
   */
  def warmup()
*/
}
