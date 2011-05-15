package oae

import java.awt._
import physics._
import java.text.DecimalFormat
import scala.collection.immutable.List

object Debug {
  var strings = List[String]()
  val x = 0
  val y = 0
  val width = 300
  val height = 200


  def int(d:Double) = d.asInstanceOf[Int]

  def str(d:Double) = new DecimalFormat("0.00").format(d)

  def clear() {strings = List[String]()}

  def out(s:String) {
    strings = strings ::: List(s)
  }

  def draw(g:Graphics2D) {

    g.translate(x, y)
    g.setColor(Color.black)
    g.fillRect(0, 0, width, height)
    g.setColor(Color.red)
    g.drawRect(0, 0, width, height)
    g.setColor(Color.green)
    var i = 10;
    val outs = strings
    strings.foreach((s:String) => {
      i += 10
      g.drawString(s, 15, y + i)
    })
    g.translate(-x, -y)
    clear
  }
}