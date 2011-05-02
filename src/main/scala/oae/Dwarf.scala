package oae


import java.awt.Graphics2D
import Images.dwarf

case class Dwarf(accel:Coord, velocity:Coord, position:Coord) {
  def accell(delta:Coord) = Dwarf(accel + delta, velocity, position)

  def simulate(time:Double) = {
//    val vel = velocity + (accel * time)
//    val pos = position + (velocity * time)
    val x = position.x

    val vel = if(x > 800) Coord(-1, 0) else if (x < 0) Coord(1,0) else velocity
    val pos = position + vel
    Dwarf(accel, vel, pos)
  }

  def draw(g:Graphics2D) {
    g.drawImage(dwarf, position.x.asInstanceOf[Int], position.y.asInstanceOf[Int], null)
  }
}