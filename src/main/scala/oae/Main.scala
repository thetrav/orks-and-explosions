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

    val canvas = new Canvas
    frame.getContentPane.add(canvas)

    canvas.setIgnoreRepaint(true)

    frame.addKeyListener(new KeyAdapter{
      override def keyPressed(e:KeyEvent) {
        input += e.getKeyCode -> System.currentTimeMillis
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          gameRunning = false
        }
      }

      override def keyReleased(e:KeyEvent) {
        input -= e.getKeyCode
      }

    })

    frame.setResizable(false)
    frame.setVisible(true)

    canvas.createBufferStrategy(2)
    val strategy = canvas.getBufferStrategy

    new Thread() {
      override def run {
        Player.init
        val camera = CenterCam
        var scene = Scene(initProps)

        var lastTime = System.currentTimeMillis
        var timeCounter = 0L
        val simulationResolution = 10L

        while(gameRunning) {
          val time = System.currentTimeMillis
          timeCounter = timeCounter + (time - lastTime)
          lastTime = time

          while(timeCounter > simulationResolution) {
            timeCounter -= simulationResolution
            Player.simulate(input)
            Physics.simulate
            camera.simulate
            Debug.clear
          }

          val g = strategy.getDrawGraphics().asInstanceOf[Graphics2D]
          g.setColor(Color.black)
          g.fillRect(0, 0, width, height)

          camera.transform(g)

          scene.draw(g)
          Player.draw(g)
          Physics.draw(g)

          camera.unTransform(g)
          Debug.draw(g)

          g.dispose
          strategy.show

          Thread.sleep(10)
        }
        System.exit(0)
      }
    }.start()
  }

  def initProps = {
    List(
      Prop(Coord(0,-10), Images.grass),
      Prop(Coord(200,20), Images.grass),
      Prop(Coord(3000,-20), Images.grass),
      Prop(Coord(-2000,10), Images.grass),
      Prop(Coord(34,12), Images.flowers),
      Prop(Coord(900,12), Images.statue)
    )
  }
}