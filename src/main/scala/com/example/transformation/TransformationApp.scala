package com.example.transformation

object TransformationApp {
  def main(args: Array[String]) {
    //starting 2 frontend nodes , 3 backend nodes
    TransformationFrontend.main(Seq("2551").toArray)
    TransformationBackend.main(Seq("2552").toArray)
    TransformationBackend.main(Array.empty)
    TransformationBackend.main(Array.empty)
    TransformationFrontend.main(Array.empty)
  }
}