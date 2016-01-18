package com.example.future

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object SumApplication extends App {

  val startTime = System.currentTimeMillis()

  val future1 = Future(timeTakingIdentityFunction(1))
  val future2 = Future(timeTakingIdentityFunction(2))
  val future3 = Future(timeTakingIdentityFunction(3))

  val future = for {
    x <- future1
    y <- future2
    z <- future3
  }  yield (x+y+z)


  future onSuccess {
    case sum =>
      val elapsedTime = (System.currentTimeMillis() - startTime) / 1000.0
      println("Sum of 1, 2 and 3 is " + sum  + " calculated in " + elapsedTime + " seconds")
  }


  def timeTakingIdentityFunction(number: Int): Int = {
    Thread.sleep(3000)
    number
  }
}