package oae

import java.awt._
import physics._
import java.text.DecimalFormat

case class Debug(pos:Coord, size:Coord, dwarf:Dwarf, camera:Camera) {
  def int(d:Double) = d.asInstanceOf[Int]

  def str(d:Double) = new DecimalFormat("0.00").format(d)

  def draw(g:Graphics2D) {

    g.translate(int(pos.x), int(pos.y))
    g.setColor(Color.black)
    g.fillRect(0, 0, int(size.x), int(size.y))
    g.setColor(Color.red)
    g.drawRect(0, 0, int(size.x), int(size.y))
    g.setColor(Color.green)

    def drawMotion(label:String, motion:Motion, pos:Coord) {
      g.translate(pos.x, pos.y)
      g.drawString(label, 0, 0)
      g.drawString("accelleration: ", 5, 20)
      g.drawString(str(motion.accelleration.x), 100,20)
      g.drawString(str(motion.accelleration.y), 150,20)

      g.drawString("velocity: ", 5, 40)
      g.drawString(str(motion.velocity.x), 100,40)
      g.drawString(str(motion.velocity.y), 150,40)

      g.drawString("position: ", 5, 60)
      g.drawString(str(motion.position.x), 100,60)
      g.drawString(str(motion.position.y), 150,60)
      g.translate(-1*pos.x, -1*pos.y)
    }
    drawMotion("Dwarf:", dwarf.motion, Coord(10,20))

    drawMotion("Camera:", camera.motion, Coord(10, 100))

    g.drawString("centerX"+ (camera.x + Main.width/2), 15, 180)
  }
}