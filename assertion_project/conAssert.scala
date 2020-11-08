import chisel3._
import chisel3.util._

class conAssert(sigB: Bool, cycles: Int, message: String) extends Module{
  val io = IO(new Bundle {
    val sigA = Input(Bool())
    val testp = Output(Bool())
  })
  
  when (io.sigA) {
    for (i <- 0 until cycles) {
      // Den helt generelle assertion
      when (sigB === true.B){
        io.testp := true.B
      } .otherwise {
        println(message) //Printer uanset
        io.testp := false.B
      }
    }
  }
}

object Main extends App {}
