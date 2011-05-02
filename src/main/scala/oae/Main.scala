package oae


import javax.swing._
import java.awt._
import java.awt.event._

object Main {
  val width = 800
  val height = 600

  var gameRunning = true

  var input = Map[Int, Long]()

  def main(args:Array[String]) {
    val frame = new JFrame("orks and explosions")
    frame.setSize(width, height)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    val canvas = new Canvas()
    frame.getContentPane().add(canvas)

    canvas.setIgnoreRepaint(true)

    canvas.addKeyListener(new KeyAdapter{
      override def keyPressed(e:KeyEvent) {
        input += e.getKeyCode() -> 1
      }

      override def keyReleased(e:KeyEvent) {
        input -= e.getKeyCode()
      }

      override def keyTyped(e:KeyEvent) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          gameRunning = false
        }
      }
    })

    frame.setResizable(false)
    frame.setVisible(true)

    canvas.createBufferStrategy(2)
    val strategy = canvas.getBufferStrategy()


    new Thread() {
      override def run() {
        var dwarf = Dwarf(Coord(0,0), Coord(0,0), Coord(400,300))
        var lastTime = System.currentTimeMillis()
        while(gameRunning) {
          val time = System.currentTimeMillis()
          val delta = time - lastTime
          lastTime = time

          def getUserAccel() = {
            if(input.contains(KeyEvent.VK_LEFT)) {
              Coord(-1,0)
            } else if (input.contains(KeyEvent.VK_RIGHT)) {
              Coord(1,0)
            } else Coord(0,0)
          }

          dwarf = dwarf.accell(getUserAccel())
          dwarf = dwarf.simulate(delta)

          val g = strategy.getDrawGraphics().asInstanceOf[Graphics2D]
          g.setColor(Color.black)
          g.fillRect(0, 0, width, height)

          dwarf.draw(g)

          g.dispose()
          strategy.show()

          Thread.sleep(10)
        }
        System.exit(0)
      }
    }.start()
  }
}