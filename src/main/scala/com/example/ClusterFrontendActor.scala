package com.example

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.typesafe.config.ConfigFactory


class ClusterFrontendActor extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case MemberUp(member) =>
      log.warning("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.warning("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.warning("Member is Removed: {} after {}",
        member.address, previousStatus)
    case _: MemberEvent =>
  }
}


object ClusterFrontendActor {
  //activator "runMain com.example.ClusterFrontendActor 0"
  def main(args: Array[String]) {
    startup(args(0))
  }

  def startup(port: String): Unit = {
    val sourceConf = ConfigFactory.load("cluster_configuration")

    val config = ConfigFactory.defaultOverrides()
      .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port))
      .withFallback(sourceConf)
    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[ClusterFrontendActor], name = "clusterFrontendActor")
  }
}