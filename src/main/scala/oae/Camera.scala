package oae

import java.awt.Graphics2D

trait Camera {

  def x:Double
  def y:Double
  def pos:Coord

  def simulate:Unit
  def transform(g:Graphics2D) {
    g.translate(x*(-1), y*(-1))
  }

  def unTransform(g:Graphics2D) {
    g.translate(x*1, y*1)
  }
}

case object CenterCam extends Camera {
  def x = Player.x - Game.width/2
  def y = Player.y - Game.height/2
  def pos = Player.pos

  def simulate {

  }
}