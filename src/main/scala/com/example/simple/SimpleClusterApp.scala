//akka-sample-cluster-scala
package com.example.simple

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory

object SimpleClusterApp {
  def main(args: Array[String]) {
    if (args.isEmpty)
      startup(Seq("2551","2552", "0"))
    else
      startup(args)
  }

  def startup(ports: Seq[String]):Unit ={
    ports foreach {port =>
      val config = ConfigFactory
        .parseString("akka.remote.netty.tcp.port="+port)
        .withFallback(ConfigFactory.load("simple_cluster"))

      val system = ActorSystem("ClusterSystem", config)
      system.actorOf(Props[SimpleClusterListener], name = "clusterListener")
    }
  }
}