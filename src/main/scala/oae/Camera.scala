package oae

import physics.Motion
import java.awt.Graphics2D

case class Camera(motion:Motion) {
  def simulate(playerMotion:Motion) = {
    val rightBound = x + Main.width - 300
    val newMotion = if(playerMotion.position.x > rightBound) {
      motion.accell(playerMotion.accelleration)
    } else {
      motion.accell(Coord(0,0))
    }
    Camera(newMotion.move)
  }

  def x = motion.position.x
  def y = motion.position.y

  def centerOn(c:Coord) = {
    val pos = c*(-1) + Coord(Main.width/2, Main.height/2)
    Camera(Motion(Coord(0,0), Coord(0,0), pos))
  }

  def transform(g:Graphics2D) {
    g.translate(x, y)
  }

  def unTransform(g:Graphics2D) {
    g.translate(x*(-1), y*(-1))
  }
}