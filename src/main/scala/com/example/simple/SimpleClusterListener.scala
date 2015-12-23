package com.example.simple

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class SimpleClusterListener extends Actor with ActorLogging {
  val cluster = Cluster(context.system)

  override def preStart() = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
    classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop() = {
    cluster.unsubscribe(self)
  }

  def receive = {
    case MemberUp(member) =>
      log.info("Memeber is Up : {}", member.address)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member.address)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is removed: {} after {}", member.address, previousStatus)
    case _: MemberEvent =>
  }
}