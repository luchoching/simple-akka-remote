package com.example.stats

import akka.actor.{Actor, ActorRef, Props, ReceiveTimeout}
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import akka.routing.FromConfig

import scala.concurrent.duration._


class StatsService extends Actor {
  val workerRouter = context.actorOf(FromConfig.props(Props[StatsWorker]), name = "workerRouter")

  def receive = {
    case StatsJob(text) if text != "" =>
      val words = text.split(" ")
      val replyTo = sender()
      val aggregator = context.actorOf(Props(classOf[StatsAggregator], words.size, replyTo))
      words foreach {
        word =>
          workerRouter.tell(ConsistentHashableEnvelope(word, word), aggregator)
      }
  }
}

class StatsAggregator(expectedResults: Int, replyTo: ActorRef) extends Actor {
  var results = IndexedSeq.empty[Int]
  context.setReceiveTimeout(3.seconds)

  def receive = {
    case wordCount: Int =>
      results = results :+ wordCount
      if (results.size == expectedResults) {
        val meanWordLength = results.sum.toDouble / results.size
        replyTo ! StatsResult(meanWordLength)
        context.stop(self)
      }
    case ReceiveTimeout =>
      replyTo ! JobFailed("Service unavaliable, try again later")
      context.stop(self)
  }
}