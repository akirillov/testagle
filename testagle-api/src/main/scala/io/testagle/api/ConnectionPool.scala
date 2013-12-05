package io.testagle.api

import sun.rmi.transport.tcp.TCPConnection

/**
 *
 * Connection pool trait being implemented on the Test Node side
 *
 */
trait ConnectionPool {

  def getConnection(): TCPConnection

}
