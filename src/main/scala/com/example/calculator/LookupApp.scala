package com.example.calculator

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.util.Random

object LookupApp {
  def main(args: Array[String]) {
    if (args.isEmpty || args.head == "Calculator")
      startRemoteCalculatorSystem()
    if (args.isEmpty || args.head == "Loopup")
      startRemoteLookupSystem()

  }

  def startRemoteCalculatorSystem(): Unit = {
    val config = ConfigFactory
      .parseString("akka.remote.netty.tcp.port=2552")
      .withFallback(ConfigFactory.load("calculator"))

    val system = ActorSystem("CalculatorSystem", config)
    system.actorOf(Props[CalculatorActor], "calculator")

    println("Started CalculatorSystem - waiting for messages")
  }

  def startRemoteLookupSystem(): Unit = {
    val config = ConfigFactory
      .parseString("akka.remote.netty.tcp.port=2553")
      .withFallback(ConfigFactory.load("calculator"))

    val system = ActorSystem("LookupSystem", config)

    val remotePath =
      "akka.tcp://CalculatorSystem@127.0.0.1:2552/user/calculator"

    val actor = system.actorOf(Props(classOf[LookupActor], remotePath), "lookupActor")

    println("Started LookupSystem")

    import system.dispatcher
    system.scheduler.schedule(1.second, 1.second) {
      if (Random.nextInt(100) % 2 == 0)
        actor ! Add(Random.nextInt(100), Random.nextInt(100))
      else
        actor ! Substract(Random.nextInt(100), Random.nextInt(100))
    }

  }
}