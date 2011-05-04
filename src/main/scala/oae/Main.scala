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
        var dwarf = Dwarf(Motion(Coord(0,0), Coord(0,0), Coord(0,0), 0.85))
        var camera = Camera(Motion(Coord(0,0), Coord(0,0), Coord(0,0), 0.95)).centerOn(dwarf.motion.position)
        var scene = Scene(initProps())

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
//            camera = camera.centerOn(dwarf.motion.position)
            camera = camera.simulate(dwarf.motion)
          }

          val g = strategy.getDrawGraphics().asInstanceOf[Graphics2D]
          g.setColor(Color.black)
          g.fillRect(0, 0, width, height)

          camera.transform(g)

          scene.draw(g)
          dwarf.draw(g)

          camera.unTransform(g)
          Debug(Coord(0,0), Coord(300,200), dwarf, camera).draw(g)

          g.dispose()
          strategy.show()

          Thread.sleep(10)
        }
        System.exit(0)
      }
    }.start()
  }

  def initProps() = {
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