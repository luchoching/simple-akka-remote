package com.example.transformation

import java.util.concurrent.atomic.AtomicInteger

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._


class TransformationFrontend extends Actor {

  var backends = IndexedSeq.empty[ActorRef]
  var jobCounter = 0

  def receive = {
    case job: TransformationJob if backends.isEmpty =>
      sender ! JobFailed("Server unavaliable, try again later", job)
    case job: TransformationJob =>
      jobCounter += 1
      backends(jobCounter % backends.size) forward job
    case BackendRegistration if !backends.contains(sender()) =>
      context watch sender()
      backends = backends :+ sender()
    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
  }
}

object TransformationFrontend {
  def main(args: Array[String]) {
    val port = if (args.isEmpty) "0" else args(0)

    val sourceConf = ConfigFactory.load("cluster_configuration")
    val config = ConfigFactory.defaultOverrides()
      .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port))
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [frontend]"))
      .withFallback(sourceConf)
    val system = ActorSystem("ClusterSystem", config)
    val frontend = system.actorOf(Props[TransformationFrontend], name = "frontend")


    val counter = new AtomicInteger

    import system.dispatcher
    system.scheduler.schedule(2.seconds, 2.seconds) {
      implicit val timeout = Timeout(5 seconds)
      (frontend ? TransformationJob("hello-" + counter.incrementAndGet())) onSuccess {
        case result => println(result)
      }
    }

  }
}