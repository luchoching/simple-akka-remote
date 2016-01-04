package com.example.clusterSharding

import akka.actor._
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.pattern.ask
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object BlogApp {
  def main(args: Array[String]) {
    if (args.isEmpty)
      startup(Seq("2551", "2552", "0"))
  }

  def startup(ports: Seq[String]): Unit = {
    ports foreach { port =>
      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
        .withFallback(ConfigFactory.load("cluster_sharding"))

      val system = ActorSystem("ClusterSystem", config)

      startupSharedJournal(system, startStore = (port == "2551"), path =
        ActorPath.fromString("akka.tcp://ClusterSystem@127.0.0.1:2551/user/store"))

      val authorListingRegion: ActorRef = ClusterSharding(system).start(
        typeName = AuthorListing.shardName,
        entityProps = AuthorListing.props(),
        settings = ClusterShardingSettings(system),
        extractEntityId = AuthorListing.idExtractor,
        extractShardId = AuthorListing.shardResolver)



    }
  }

  def startupSharedJournal(system: ActorSystem, startStore: Boolean, path: ActorPath): Unit = {

    if (startStore)
      system.actorOf(Props[SharedLeveldbStore], "store")

    import system.dispatcher
    implicitly val timeout = Timeout(15.seconds)

    val f = (system.actorSelection(path) ? Identify(None))
    f.onSuccess {
      case ActorIdentity(_, Some(ref)) => SharedLeveldbJournal.setStore(ref, system)
      case _ =>
        system.log.error("Shared journal not started at {}", path)
        system.terminate()
    }
    f.onFailure {
      case _ =>
        system.log.error("Lookup of shared journal at {} time out", path)
        system.terminate()
    }


  }
}