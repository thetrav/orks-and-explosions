package oae


import javax.swing._
import java.awt._
import java.awt.event._
import physics._
import collection.immutable.List

object Game {
  val width = 900
  val height = 600
  val gridSize = 30

  var gameRunning = true
  var drawPhysics = false
  var mousePos = Coord(0,0)


  var input = Map[Int, Long]()

  val camera = CenterCam

  def run {
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
    val listener = new KeyAdapter{
      override def keyPressed(e:KeyEvent) {
        input += e.getKeyCode -> System.currentTimeMillis
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
          gameRunning = false
        }
        if(e.getKeyCode() == KeyEvent.VK_BACK_QUOTE) {
          Debug.toggle()
        }
        if(e.getKeyCode() == KeyEvent.VK_P) {
          drawPhysics = !drawPhysics
        }
      }

      override def keyReleased(e:KeyEvent) {
        input -= e.getKeyCode
      }

    }
    frame.addKeyListener(listener)
    canvas.addKeyListener(listener)


    val mouseListener = new MouseAdapter {
      override def mouseMoved(e:MouseEvent) {
        mousePos = Coord(e.getX.asInstanceOf[Double], e.getY.asInstanceOf[Double])
      }
    }
    frame.addMouseMotionListener(mouseListener)
    canvas.addMouseMotionListener(mouseListener)

    frame.setResizable(false)
    frame.setVisible(true)

    canvas.createBufferStrategy(2)
    val strategy = canvas.getBufferStrategy

    new Thread {
      override def run {
        Player.init
        var scene = Scene(initProps)

        var lastTime = System.currentTimeMillis
        var timeCounter = 0L
        val simulationResolution = 10L
        var debugUpdateCounter = 0
        var debugUpdateRate = 100

        while(gameRunning) {
          val time = System.currentTimeMillis
          timeCounter = timeCounter + (time - lastTime)
          lastTime = time

          while(timeCounter > simulationResolution) {
            debugUpdateCounter += 1
            if (debugUpdateCounter > debugUpdateRate) {
              debugUpdateCounter = 0
              Debug.clear
            }
            timeCounter -= simulationResolution
            Player.simulate(input, (mousePos+camera.pos))
            Physics.simulate
            camera.simulate
          }

          Debug.out("cameraPos:"+(camera.pos))

          val g = strategy.getDrawGraphics().asInstanceOf[Graphics2D]
          g.setColor(Color.black)
          g.fillRect(0, 0, width, height)

          camera.transform(g)


          scene.draw(g)
          Player.draw(g)
          if(drawPhysics) Physics.draw(g)
          drawGrid(g)

          camera.unTransform(g)
          g.setColor(Color.green)

          Player.drawInventory(g)
          Debug.draw(g)

          g.dispose
          strategy.show

          Thread.sleep(10)
        }
        System.exit(0)
      }
    }.start()
  }

  def drawGrid(g:Graphics) {
    g.setColor(new Color(255,255,255, 100))
    val x = Player.x - (Player.x % gridSize) - width/2
    val y = Player.y - (Player.y % gridSize) - height/2
    g.translate(x.asInstanceOf[Int], y.asInstanceOf[Int])
    (1 to  (width/gridSize)).foreach(x => {
      (1 to (height/gridSize)).foreach(y => {
        g.fillOval(x*gridSize-1, y*gridSize-1, 3, 3)
      })
    })
  }

  def initProps = {
    List(
      Prop(Coord(0,178), Images.decals(0)),
      Prop(Coord(200,178), Images.decals(0)),
      Prop(Coord(140,178), Images.decals(0)),
      Prop(Coord(1000,178), Images.decals(0)),
      Prop(Coord(1543,178), Images.decals(0)),

      Prop(Coord(-100,175), Images.decals(1)),


      Prop(Coord(800,175), Images.decals(2)),
      Prop(Coord(850,165), Images.decals(3)),
      Prop(Coord(900,175), Images.decals(4)),
      Prop(Coord(777,175), Images.decals(6)),
      Prop(Coord(1312,175), Images.decals(5))
    )
  }
}