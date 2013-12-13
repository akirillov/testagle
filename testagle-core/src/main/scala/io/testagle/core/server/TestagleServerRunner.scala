package io.testagle.core.server

import io.testagle.core.TestagleServer

/**
 *
 * TODO: document this ASAP!!!
 *
 */
object TestagleServerRunner extends App{
  override def main(args: Array[String]) {

    TestagleServer(1313, getName)

    println("dispatching server started")
  }

  def getName = "test node"
}
