package com.example.tcpConnection

import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Actor, Props, ActorRef}
import akka.io.{IO, Tcp}
import akka.util.ByteString

object ConcentratorClient {
  def main(args: Array[String]) {
    val system = ActorSystem("mySystem")
    val myActor = system.actorOf(Props(
      classOf[ConcentratorClient], new InetSocketAddress("127.0.0.1",10000)), "myactor2")
  }


  def props(remote: InetSocketAddress): Unit = {
    Props(classOf[ConcentratorClient], remote)
  }
}

class ConcentratorClient(remote: InetSocketAddress) extends Actor {
  import Tcp._
  import context.system

  IO(Tcp) ! Connect(remote)

  def receive = {
    case CommandFailed(_: Connect) =>
      println("connect failed")
      context stop self

    case c @ Connected(remote, local) =>
      println(c)
      val connection = sender()
      connection ! Register(self)
      context become {
        case data: ByteString =>
          connection ! Write(data)
        case CommandFailed(w: Write) =>
          // O/S buffer was full
          println("write failed")
        case Received(data) =>
          println( data)
        case "close" =>
          connection ! Close
        case _: ConnectionClosed =>
          println("connection closed")
          context stop self
      }
  }
}
