import chisel3._
import chisel3.iotesters._
import org.scalatest._

import Types._

//The code below is using ScalaTest
/*
class AluAccuTest extends FlatSpec with Matchers {
  "Integers" should "add" in {
    val i = 2
    val j = 3
    i + j should be (5)
  }

  "Integers" should "subtract" in {
    val i = 2
    val j = 3
    i - j should be (-1)
  }

  "Integers" should "multiply" in {
    val i = 2
    val j = 3
    i * j should be (6)
  }
}
*/

//The code below is using iotesters
class AluAccuTest(dut: AluAccuChisel) extends PeekPokeTester(dut) {
  poke(dut.io.din, 2.U)
  poke(dut.io.ena, 1)
  poke(dut.io.op, add)
  step(1)
  poke(dut.io.ena, 1)
  poke(dut.io.din, 2.U)
  step(1)
  println("Accumulator shows " + peek(dut.io.accu).toString)
}

object AluAccuTest extends App {
  chisel3.iotesters.Driver(() => new AluAccuChisel(32)) { c => new AluAccuTest(c)}
}