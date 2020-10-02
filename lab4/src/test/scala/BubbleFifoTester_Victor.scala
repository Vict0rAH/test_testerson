import chisel3._
import chiseltest._
import org.scalatest._

/**
  * Author: Martin Schoeberl (martin@jopdesign.com)
  *
  * Just a template to get started with ChiselTest
  */

class BubbleFifoTester_Victor extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The BubbleFifo"

  it should "move data from input to output" in {
    test(new BubbleFifo(32, 4)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq

        enq.write.poke(true.B)
        deq.read.poke(false.B)
        enq.din.poke(123.U)
        dut.clock.step(1)
        enq.write.poke(false.B)
        
        while (deq.empty.peek.litValue == 1) {
          dut.clock.step(1)
        }
        deq.dout.expect(123.U)
      }
    }
  }

  it should "be empty after reset" in {
    test(new BubbleFifo(32, 4)) {
      dut => {
        val deq = dut.io.deq

        dut.reset.poke(true.B)
        dut.clock.step(1)
        deq.empty.expect(true.B)
      }
    }
  }

  it should "signal when full" in {
    test(new BubbleFifo(32,4)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq

        enq.write.poke(true.B)
        deq.read.poke(false.B)
        while (enq.full.peek.litValue == 0){
          dut.io.enq.din.poke(1.U)
          dut.clock.step(1)
        }
        dut.io.enq.full.expect(true.B)
      }
    }
  }

  it should "output order equals input order" in {
    test(new BubbleFifo(32, 4)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq
        val order = Vector(1.U, 2.U, 3.U, 4.U)

        for (i <- 0 until 4) {
          enq.write.poke(true.B)
          deq.read.poke(false.B)
          enq.din.poke(order(i))
          while (enq.full.peek.litValue == 0){
            dut.clock.step(1)
          }
        }
        
        for (i <- 0 until 4) {
          
          enq.write.poke(false.B)
          deq.read.poke(true.B)
          deq.dout.expect(order(i))

          while (deq.empty.peek.litValue == 0){
            dut.clock.step(1)
          }
        }

      }
    }
  }

  it should "handle corner cases" in {
    throw new Error("Missing test")
  }

  it should "ignore data when full" in {
    throw new Error("Missing test")
  }

}
