package com.example.simple

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class SimpleClusterListener2 extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart() = {
    cluster.subscribe(self, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop() = {
    cluster.unsubscribe(self)
  }

  def receive = {
    case state: CurrentClusterState =>
      log.info("Current members: {}", state.members.mkString(", "))
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
    log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
    log.info("Member is Removed: {} after {}",
      member.address, previousStatus)
    case _: MemberEvent => // ignore
  }
}