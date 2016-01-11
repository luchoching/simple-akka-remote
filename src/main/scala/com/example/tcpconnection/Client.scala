package com.example.tcpconnection
import java.net.InetSocketAddress

import akka.actor.{Props, ActorLogging, ActorRef, Actor}
import akka.io.{IO, Tcp}
import akka.util.ByteString


object Client {

  case object ConnFailed
  case object ConnClosed
  case object WriteFailed

  def props(remote: InetSocketAddress, replies: ActorRef) =
    Props(classOf[Client], remote, replies)
}

class Client(remote: InetSocketAddress, listener: ActorRef)
  extends Actor with ActorLogging {

  import Tcp._
  import context.system
  import Client._

  IO(Tcp) ! Connect(remote)

  def receive = {
    case CommandFailed(_: Connect) =>
      listener ! ConnFailed
      context stop self
    case c @ Connected(remote, local) =>
      listener ! c
      val connection = sender()
      connection ! Register(self)
      context become {
        //send data to server
        case data: ByteString =>
          connection ! Write(data)
        case CommandFailed(w: Write) =>
          // O/S buffer was full
          listener ! WriteFailed
        case Received(data) =>
          listener ! data
        case "close" =>
          connection ! Close
        case _: ConnectionClosed =>
          listener ! ConnClosed
          context stop self
      }
  }



}