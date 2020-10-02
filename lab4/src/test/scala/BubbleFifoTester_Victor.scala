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
    test(new BubbleFifo(32,4)) {
      
    }
  }

  it should "signal when full" in {
    test(new BubbleFifo(32,4)) {
      dut => {
        for (i <- 0 until 4) {
          dut.io.enq.din.poke(1.U)
          dut.io.enq.write.poke(true.B)
          dut.clock.step(1)
        }
        dut.io.enq.full.expect(true.B)
      }
    }
  }

  it should "find yourself 3 more test cases to test" in {
    throw new Error("Missing tests")
  }

}
