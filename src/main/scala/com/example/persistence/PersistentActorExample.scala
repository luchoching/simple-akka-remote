package com.example.persistence

import akka.actor.{ActorSystem, Props}
import akka.persistence.{PersistentActor, SnapshotOffer}
import com.typesafe.config.ConfigFactory

case class Cmd(data: String)

case class Evt(data: String)


case class ExampleState(events: List[String] = Nil) {
  def updated(evt: Evt): ExampleState = copy(evt.data :: events)

  def size: Int = events.length

  override def toString: String = events.reverse.toString
}


class ExamplePersistentActor extends PersistentActor {
  var state = ExampleState()

  override def persistenceId = "sample-id-1"

  def receiveRecover: Receive = {
    case evt: Evt => updateState(evt)
    case SnapshotOffer(_, snapshot: ExampleState) => state = snapshot
  }

  def updateState(event: Evt): Unit =
    state = state.updated(event)

  def receiveCommand: Receive = {
    case Cmd(data) =>
      persist(Evt(s"${data}-${numEvents}"))(updateState)
      persist(Evt(s"${data}-${numEvents + 1}")) { event =>
        updateState(event)
        context.system.eventStream.publish(event)
      }
    case "snap" => saveSnapshot(state)
    case "print" => println(state)
  }

  def numEvents =
    state.size
}

object PersistentActorExample extends App {
  val sourceConf = ConfigFactory.load("persistence")

  val system = ActorSystem("example", sourceConf)
  val persistentActor = system.actorOf(Props[ExamplePersistentActor], "persistentActor-4-scala")

  persistentActor ! Cmd("foo")
  persistentActor ! Cmd("baz")
  persistentActor ! Cmd("bar")
  persistentActor ! "snap"
  persistentActor ! Cmd("buzz")
  persistentActor ! "print"

  Thread.sleep(1000)
  system.terminate()
}