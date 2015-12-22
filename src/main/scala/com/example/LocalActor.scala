package com.example

import java.io.File

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

//local actor which listens on any free port
class LocalActor extends Actor {
  //@throw[Exception] (classOf[Exception])

  override def preStart(): Unit = {
    val remoteActor = context.actorSelection("akka.tcp://RemoteSystem@127.0.0.1:5150/user/remote")
    println("That's a remote:" + remoteActor)
    remoteActor ! "hi"
  }

  override def receive: Receive = {
    case msg: String =>
      println("got msg from remote " + msg)
  }
}

object LocalActor {
  def main(args: Array[String]) {
    val configFile = getClass.getClassLoader.getResource("local_configuration.conf")
      .getFile

    val config = ConfigFactory.parseFile(new File(configFile))
    val system = ActorSystem("ClientSystem", config)
    val localActor = system.actorOf(Props[LocalActor], name = "local")
  }
}