package oae

import physics.Motion
import java.awt.Graphics2D

/**
 *
 *
 * I want to center on a point just in front of where the character is moving to.
 *
 * I only want to re-center if it looks like the player is going to run off screen
 *
 * I want to move smoothly.
 *
 *
 */

case class Camera(motion:Motion) {

  def simulate(playerMotion:Motion) = {
    val pPos = playerMotion.position.x
    val pVel = playerMotion.velocity.x
    val tollerance = 200

    val accell = if(pVel*pVel > 0) {
      val target = pPos + (pVel*5)
      val currentCenter = x + Main.width/2
      if(currentCenter > target+tollerance && currentCenter < target-tollerance) {
        //slow down
        playerMotion.accelleration * 0.1
      } else if (currentCenter < target - tollerance || currentCenter > target + tollerance) {
        //speed up
        playerMotion.accelleration * 0.5
      } else if (motion.accelleration.x * motion.accelleration.x > 0) {
        //maintain
        playerMotion.accelleration * 0.3
      } else {
        Coord(0,0)
      }
    } else {
      Coord(0,0)
    }

    val newMotion = motion.accell(accell)

    Camera(newMotion.move)
  }

  def x = motion.position.x
  def y = motion.position.y
  def friction = motion.friction

  def centerPoint(c:Coord) = c - Coord(Main.width/2, Main.height/2)

  def centerOn(c:Coord) = {
    Camera(Motion(Coord(0,0), Coord(0,0), centerPoint(c), friction))
  }

  def transform(g:Graphics2D) {
    g.translate(x*(-1), y*(-1))
  }

  def unTransform(g:Graphics2D) {
    g.translate(x, y)
  }
}