package com.example.tcpconnection

import java.net.InetSocketAddress

import akka.actor.{Props, ActorSystem}

object TestTcpApp {
  def main(args: Array[String]) {
    val system = ActorSystem("testsystem")
    val handler = system.actorOf(Props[Handler], "handler")
    val props = Client.props(new InetSocketAddress("127.0.0.1", 10000), handler)
    val client = system.actorOf(props, "client")
  }
}