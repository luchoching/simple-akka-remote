package com.example.calculator

import akka.actor._
import scala.concurrent.duration._

class LookupActor(path: String) extends Actor {

  sendIdentifyRequest()

  def sendIdentifyRequest():Unit = {
    context.actorSelection(path) ! Identify(path)
    import context.dispatcher
    context.system.scheduler.scheduleOnce(3.seconds, self, ReceiveTimeout)
  }

  def receive = identifying

  def identifying: Receive = {
    case ActorIdentity(`path`, Some(actor)) =>
      context watch actor
      context.become(active(actor))
    case ActorIdentity(`path`, None) =>
      println(s"Remote actor not available: $path")
    case ReceiveTimeout =>
      sendIdentifyRequest()
    case _ =>
      println("not ready yet")
  }

  def active(actor: ActorRef): Receive = {
    case op: MathOp => actor ! op
    case result: MathResult => result match {
      case AddResult(n1, n2, r) =>
        printf("Add result: %d + %d = %d\n", n1, n2, r)
      case SubstractResult(n1, n2, r) =>
        printf("Sub result: %d - %d = %d\n", n1, n2, r)
    }
    case Terminated(`actor`) =>
      println("Calculator terminated")
      sendIdentifyRequest()
      context.become(identifying)
    case ReceiveTimeout =>
  }

}
