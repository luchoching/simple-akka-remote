package com.example.hello

import akka.actor.{Actor, Props}

class HelloWorld extends Actor {
  override def preStart = {
    val greeter = context.actorOf(Props[Greeter], "greeter")
    greeter ! Greeter.Greet
  }

  def receive = {
    case Greeter.Done => context.stop(self)
  }
}