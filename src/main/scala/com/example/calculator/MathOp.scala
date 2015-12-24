package com.example.calculator

trait MathOp

final case class Add(num1: Int, num2: Int) extends MathOp

final case class Substract(num1: Int, num2: Int) extends MathOp

final case class Multiply(num1: Int, num2: Int) extends MathOp

final case class Divide(num1: Int, num2: Int) extends MathOp

trait MathResult

final case class AddResult(num1: Int, num2: Int, result: Int) extends MathResult

final case class SubstractResult(num1: Int, num2: Int, result: Int) extends MathResult

final case class MultiplicationResult(num1: Int, num2: Int, result: Int) extends MathResult

final case class DivisionResult(num1: Int, num2: Int, result: Int) extends MathResult
