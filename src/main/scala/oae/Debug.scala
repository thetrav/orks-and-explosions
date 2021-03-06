package oae

import java.awt._
import physics._
import java.text.DecimalFormat
import scala.collection.immutable.List

object Debug {

  val height = 300
  var strings = List[String]()
  var show = false
  var accept = true
  def toggle() {
    show = !show
  }

  def int(d:Double) = d.asInstanceOf[Int]

  def str(d:Double) = new DecimalFormat("0.00").format(d)

  def out(s:String) {
    if(accept) strings = strings ::: List(s)
  }

  def draw(g:Graphics2D) {
    if(show) {
      g.setColor(new Color(0, 0, 0, 80))
      g.fillRect(0, 0, Game.width, height)
      g.setColor(Color.red)
      g.drawRect(0, 0, Game.width, height)
      g.setColor(Color.green)

      var i = 0
      for(s:String <- strings) {
        g.drawString(s, 10, 10+i)
        i+= 10
      }
    }
    accept = false
  }

  def clear {
    strings = List[String]()
    accept = true
  }
}