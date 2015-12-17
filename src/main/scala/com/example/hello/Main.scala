package com.example.hello


object Main {
  def main(args: Array[String]) {
    akka.Main.main(Array(classOf[HelloWorld].getName))
  }
}


