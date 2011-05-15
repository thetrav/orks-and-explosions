package oae

import physics._
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

object Camera {

  var coord = Coord(0,0)
  def x = coord.x.asInstanceOf[Int]
  def y = coord.y.asInstanceOf[Int]

  def centerPoint(c:Coord) = c - Coord(Main.width/2, Main.height/2)

  def centerOn(c:Coord) {
    coord = centerPoint(c)
  }

  def transform(g:Graphics2D) {
    g.translate(x*(-1), y*(-1))
  }

  def unTransform(g:Graphics2D) {
    g.translate(x, y)
  }
}