package oae


import javax.swing._
import java.awt._
import java.awt.event._
import physics._
import collection.immutable.List

object Main {
  val width = 1680
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
        Player.init()
        Scene.initProps()

        var lastTime = System.currentTimeMillis()
        var timeCounter = 0L
        val simulationResolution = 10L

        while(gameRunning) {
          val time = System.currentTimeMillis()
          timeCounter = timeCounter + (time - lastTime)
          lastTime = time

          while(timeCounter > simulationResolution) {
            Debug.clear
            timeCounter -= simulationResolution
            Player.simulate()
            Space.simulate()
            Camera.centerOn(Player.coord)
          }

          val g = strategy.getDrawGraphics().asInstanceOf[Graphics2D]
          g.setColor(Color.black)
          g.fillRect(0, 0, width, height)

          Camera.transform(g)

          Scene.draw(g)
          Player.draw(g)
          Space.draw(g)

          Camera.unTransform(g)
          g.setColor(Color.green)
          Debug.draw(g)

          g.dispose()
          strategy.show()

          Thread.sleep(10)
        }
        System.exit(0)
      }
    }.start()
  }


}