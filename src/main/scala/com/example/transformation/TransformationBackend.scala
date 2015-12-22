package com.example.transformation

import akka.actor.{Actor, ActorSystem, Props, RootActorPath}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.{Cluster, Member, MemberStatus}
import com.typesafe.config.ConfigFactory

class TransformationBackend extends Actor {

  val cluster = Cluster(context.system)

  //subscribe to cluster changes, MemberUp
  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  //re-subscribe when restart
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case TransformationJob(text) => sender ! TransformationResult(text.toUpperCase)
    case state: CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up) foreach register
    case MemberUp(m) => register(m)
  }

  def register(member: Member): Unit = {
    if (member.hasRole("frontend"))
      context.actorSelection(RootActorPath(member.address) / "user" / "frontend") !
        BackendRegistration
  }
}

object TransformationBackend {
  def main(args: Array[String]) {
    val port = if (args.isEmpty) "0" else args(0)

    val sourceConf = ConfigFactory.load("cluster_configuration")
    val config = ConfigFactory.defaultOverrides()
      .withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port))
      .withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
      .withFallback(sourceConf)

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[TransformationBackend], name = "backend")
  }
}