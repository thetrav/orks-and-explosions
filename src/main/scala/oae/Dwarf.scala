package oae


import java.awt.Graphics2D
import Images.dwarf
import oae.physics.Motion

case class Dwarf(motion:Motion) {

  def accell(delta:Coord) = Dwarf(motion.accell(delta))

  def simulate() = {
    Dwarf(motion.move())
  }

  def x = motion.position.x
  def y = motion.position.y

  def draw(g:Graphics2D) {
    g.drawImage(dwarf, x.asInstanceOf[Int], y.asInstanceOf[Int], null)
  }
}