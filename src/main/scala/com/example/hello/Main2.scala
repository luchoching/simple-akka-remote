package com.example.hello

import akka.actor._

object Main2 {
  def main(args: Array[String]) {
    val system = ActorSystem("Hello")
    val a = system.actorOf(Props[HelloWorld], "helloWorld")

    system.actorOf(Props(classOf[Terminator], a), "terminator")
  }

  class Terminator(ref: ActorRef) extends Actor with ActorLogging {
    context watch ref

    def receive = {
      case Terminated(_) =>
        log.info("{} has terminated, shutting down system", ref.path)
        context.system.terminate()
    }

  }

}