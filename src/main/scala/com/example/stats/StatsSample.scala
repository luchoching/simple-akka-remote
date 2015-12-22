package com.example.stats

import akka.actor._
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, MemberStatus}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.concurrent.forkjoin.ThreadLocalRandom

object StatsSample {
  def main(args: Array[String]) {
    if (args.isEmpty) {
      startup(Seq("2551", "2552", "0"))
    } else {
      startup(args)
    }
  }

  def startup(ports: Seq[String]): Unit = {
    ports foreach { port =>
      val sourceConf = ConfigFactory.load("stats1")
      val config = ConfigFactory.defaultOverrides()
        .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port))
        .withFallback(ConfigFactory.parseString("akka.cluster.roles = [compute]"))
        .withFallback(sourceConf)
      val system = ActorSystem("ClusterSystem", config)

      system.actorOf(Props[StatsWorker], name = "statsWorker")
      system.actorOf(Props[StatsService], name = "statsService")
    }
  }
}

object StatsSampleClient {
  def main(args: Array[String]) {
    val system = ActorSystem("ClusterSystem")
    system.actorOf(Props(classOf[StatsSampleClient], "/user/statsService"), "client")
  }
}


class StatsSampleClient(servicePath: String) extends Actor {
  val cluster = Cluster(context.system)
  val servicePathElements = servicePath match {
    case RelativeActorPath(elements) => elements
    case _ => throw new IllegalArgumentException(
      "servicePath [%s] is not a valid relative path" format servicePath)
  }

  import context.dispatcher

  val tickTask = context.system.scheduler.schedule(2.seconds, 2.seconds, self, "tick")

  var nodes = Set.empty[Address]

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberEvent], classOf[ReachabilityEvent])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
    tickTask.cancel()
  }

  def receive = {
    case "tick" if nodes.nonEmpty =>
      val address = nodes.toIndexedSeq(ThreadLocalRandom.current.nextInt(nodes.size))
      val service = context.actorSelection(RootActorPath(address) / servicePathElements)
      service ! StatsJob("this is the text that will be analyzed")
    case result: StatsResult =>
      print(result)
    case failed: JobFailed =>
      println(failed)
    case state: CurrentClusterState =>
      nodes = state.members.collect {
        case m if m.hasRole("compute") && m.status == MemberStatus.Up => m.address
      }
    case MemberUp(m) if m.hasRole("compute") => nodes += m.address
    case other: MemberEvent => nodes -= other.member.address
    case UnreachableMember(m) => nodes -= m.address
    case ReachableMember(m) if m.hasRole("compute") => nodes += m.address
  }
}




