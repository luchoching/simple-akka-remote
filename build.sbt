name := """simple-akka-remote"""

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaV = "2.4.1"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,

    "com.typesafe.akka" %% "akka-persistence" % akkaV,
    "org.iq80.leveldb" % "leveldb" % "0.7",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",


  "com.typesafe.akka" %% "akka-remote" % akkaV,
  "com.typesafe.akka" %% "akka-cluster" % akkaV,
  "com.typesafe.akka" % "akka-cluster-metrics_2.11" % akkaV
  )
}

