package oae

import javax.swing.{JPanel, JFrame}
import java.awt.{Color, Graphics2D, Graphics}
import java.awt.event.{MouseEvent, MouseAdapter}
import java.awt.geom.{AffineTransform, PathIterator, Area, Rectangle2D}

object DigBox {
   def run {
     val frame = new JFrame("Dig Sandbox")

     frame.setSize(800,600)
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

     val right = 600
     val left = 30
     val top = 30
     val bottom = 300

//     val area1 = new Area(new Rectangle2D.Double(30,30,100,100))
//     val area2 = new Area(new Rectangle2D.Double(70, 70, 100, 20))
//     val merged = new Area(new Rectangle2D.Double(30,30,100,100))
//     merged.add(area2)

     var shape = Shape(List(
       Coord(left,bottom),
       Coord(left,top),
       Coord(right,top),
       Coord(right,bottom)
     ))

     var mPos = Coord(0,0)

     val panel = new JPanel() {
       override def paint(g1:Graphics) {
         val g = g1.asInstanceOf[Graphics2D]
         g.setColor(Color.white)
         g.fillRect(0,0,800,600)

         g.setColor(Color.red)
         g.draw(shape.shape)

         g.setColor(Color.blue)
         shape.points.foreach(c => {
           g.fillOval(c.x.asInstanceOf[Int]-2, c.y.asInstanceOf[Int]-2,5,5)
         })

         g.setColor(Color.green)
         val x = mPos.x - (mPos.x % 30)
         val y = mPos.y - (mPos.y % 30)
         g.fillOval(x.asInstanceOf[Int]-2, y.asInstanceOf[Int]-2, 5, 5)
       }

     }

     frame.getContentPane.add(panel)

     def dig {
       shape = shape.dig(mPos)
     }

     def repaint {
       frame.invalidate
       frame.validate
       frame.repaint()
     }

     panel.addMouseListener(new MouseAdapter {
       override def mouseClicked(e:MouseEvent) {
         println("click!" + e.getX + " " + e.getY)
         dig
         repaint
       }
     })

     panel.addMouseMotionListener(new MouseAdapter {
       override def mouseMoved(e: java.awt.event.MouseEvent) {
         val x = e.getX - (e.getX % 30)
         val y = e.getY - (e.getY % 30)
         mPos = Coord(x,y)
         repaint
       }
     })

     frame.setVisible(true)
   }
}