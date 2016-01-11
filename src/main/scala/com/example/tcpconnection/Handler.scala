package com.example.tcpconnection

import akka.actor.{ActorLogging, Actor}
import akka.io.Tcp.Connected
import akka.util.{ByteStringBuilder, ByteString}

class Handler extends Actor with ActorLogging{
  def receive = {
    case Client.ConnFailed =>
      log.warning("connection failed")
    case c @ Connected(remote, local) =>
      log.info("connected to server: " + remote)

      val bsb = new ByteStringBuilder();
      bsb.putBytes("Hello from client handler".getBytes());
      sender ! bsb.result()

    //received data from server
    case data: ByteString =>
      log.info(data.utf8String)
    case Client.WriteFailed =>
      log.warning("write failed")
    case Client.ConnClosed =>
      log.warning("Connection closed")
      //try to reconnect

  }
}