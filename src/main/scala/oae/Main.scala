package oae

import javax.imageio.ImageIO
import javax.swing._
import java.awt._
import java.io.File

object Main {
  val width = 800
  val height = 600

  val dwarf = ImageIO.read(getClass().getResourceAsStream("dwarf.png"))

  def main(args:Array[String]) {
    val frame = new JFrame("orks and explosions")
    frame.setSize(width, height)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    val canvas = new JPanel{
      override def paintComponent(g1:Graphics) {
        val g = g1.asInstanceOf[Graphics2D]
        g.setColor(Color.black)
        g.fillRect(0, 0, width, height)

        g.drawImage(dwarf, 0, 0, frame)
      }
    }

    frame.getContentPane().add(canvas)

    frame.setVisible(true)
  }
}