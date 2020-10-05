import chisel3._
import chiseltest._
import org.scalatest._

/**
  * Author: Martin Schoeberl (martin@jopdesign.com)
  *
  * Just a template to get started with ChiselTest
  */

object Test_data {
  val size = 32
  val depth = 4
}

class VictorTester extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "The BubbleFifo"

  it should "move data from input to output" in {
    test(new BubbleFifo(Test_data.size, Test_data.depth)) {
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
    test(new BubbleFifo(Test_data.size, Test_data.depth)) {
      dut => {
        dut.reset.poke(true.B)
        dut.clock.step(1)
        dut.reset.poke(false.B)
        dut.clock.step(1)
        dut.io.deq.empty.expect(true.B)
      }
    }
  }

  it should "signal when full" in {
    test(new BubbleFifo(Test_data.size, Test_data.depth)) {
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
    test(new BubbleFifo(Test_data.size, Test_data.depth)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq
        val order = Vector(1.U, 2.U, 3.U, 4.U)
        
        for (i <- 0 until Test_data.depth) {
          enq.write.poke(true.B)
          deq.read.poke(false.B)
          enq.din.poke(order(i))
          dut.clock.step(2)
        }

        for (i <- 0 until Test_data.depth) {
          enq.write.poke(false.B)
          deq.read.poke(true.B)
          deq.dout.expect(order(i))
          dut.clock.step(2)
        }
      }
    }
  }

  it should "handle corner cases" in {
    // Relevant corner cases
    val corner = List(429467295.U, 0.U)

    test(new BubbleFifo(Test_data.size, Test_data.depth)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq

        for (i <- 0 until 2){
          enq.write.poke(true.B)
          deq.read.poke(false.B)
          enq.din.poke(corner(i))
          dut.clock.step(1)
          enq.write.poke(false.B)
          while (deq.empty.peek.litValue == 1) {
            dut.clock.step(1)
          }
          deq.dout.expect(corner(i))

          deq.read.poke(true.B)
          dut.clock.step(1)
          while (enq.full.peek.litValue == 1) {
            dut.clock.step(1)
          }
          deq.dout.expect(0.U)
        }


      }
    }
  }

  it should "ignore new input data when full" in {
    test(new BubbleFifo(Test_data.size, Test_data.depth)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq

        enq.write.poke(true.B)
        deq.read.poke(false.B)
        enq.din.poke(4.U)
        dut.clock.step(1)
        enq.din.poke(8.U)
        while (deq.empty.peek.litValue == 1) {
          dut.clock.step(1)
        }
        deq.dout.expect(4.U)
      }
    }
  }

  it should "concurrently write and read" in {
    test(new BubbleFifo(Test_data.size, Test_data.depth)) {
      dut => {
        val enq = dut.io.enq
        val deq = dut.io.deq

        def writer() {
          enq.write.poke(true.B)
          deq.read.poke(false.B)
          enq.din.poke(1.U)
          while (enq.full.peek.litValue == 0){
            dut.clock.step(1)
          }
          enq.write.poke(false.B)
          deq.dout.expect(1.U)
        }

        def reader() {
          enq.write.poke(false.B)
          deq.read.poke(true.B)
          while (deq.empty.peek.litValue == 0){
            dut.clock.step(1)
          }
          deq.read.poke(false.B)
          deq.dout.expect(0.U)
        }

        fork {
          writer()
        }
        reader()
      }
    }
  }

}
