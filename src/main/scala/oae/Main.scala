package oae


import javax.swing._
import java.awt._
import java.awt.event._
import physics._

object Main {
  val width = 800
  val height = 600

  var gameRunning = true

  var input = Map[Int, Long]()

  def main(args:Array[String]) {
    val frame = new JFrame("orks and explosions")
    frame.setSize(width, height)
    frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    frame.addWindowListener(new WindowAdapter {
      override def windowClosed(e:WindowEvent) {
        gameRunning = false
      }
    })

    val canvas = new Canvas()
    frame.getContentPane().add(canvas)

    canvas.setIgnoreRepaint(true)

    frame.addKeyListener(new KeyAdapter{
      override def keyPressed(e:KeyEvent) {
        input += e.getKeyCode() -> 1
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          gameRunning = false
        }
      }

      override def keyReleased(e:KeyEvent) {
        input -= e.getKeyCode()
      }

    })

    frame.setResizable(false)
    frame.setVisible(true)

    canvas.createBufferStrategy(2)
    val strategy = canvas.getBufferStrategy()


    new Thread() {
      override def run() {
        var dwarf = Dwarf(Motion(Coord(0,0), Coord(0,0), Coord(400,300)))
        var lastTime = System.currentTimeMillis()
        var timeCounter = 0L
        val simulationResolution = 10L

        while(gameRunning) {
          val time = System.currentTimeMillis()
          timeCounter = timeCounter + (time - lastTime)
          lastTime = time


          dwarf = dwarf.accell(if(input.contains(KeyEvent.VK_LEFT)) {
              Coord(-1,0)
            } else if (input.contains(KeyEvent.VK_RIGHT)) {
              Coord(1,0)
            } else Coord(0,0)
          )

          while(timeCounter > simulationResolution) {
            timeCounter -= simulationResolution
            dwarf = dwarf.simulate()
          }

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