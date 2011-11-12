package oae

import java.lang.Thread._
import java.awt.event.{WindowEvent, WindowAdapter}
import java.awt.image.BufferStrategy
import java.awt._
import javax.swing.{JPanel, WindowConstants, JFrame}
import javax.imageio.ImageIO
import java.io.File

object Main {
  val width = 800
  val height = 600

  var isRunning = true;
  val config = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                  .getDefaultScreenDevice()
                                  .getDefaultConfiguration()

  val dwarf = ImageIO.read(new File("dwarf.png"));

  def main(args: Array[String]) {
    val frame = new JFrame("Orks & Explosions")
    frame.addWindowListener(new WindowAdapter {
      override def windowClosing(e:WindowEvent) {
              isRunning = false;
      }
    })
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    frame.setSize(width, height)

    val canvas = new JPanel(){
      override def paintComponent(g1:Graphics) {
        val g = g1.asInstanceOf[Graphics2D]
        g.setColor(Color.black)
        g.fillRect(0, 0, width, height)

        g.drawImage(dwarf, 100, 100, frame)
      }
    };

    frame.add(canvas, 0)


    frame.setVisible(true)
  }
}