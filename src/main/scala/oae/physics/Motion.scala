package oae.physics

import oae.Coord

case class Motion(accelleration:Coord, velocity:Coord, position:Coord, friction:Double) {

  def accell(newValue:Coord) = Motion(newValue, velocity, position, friction)

  def cutout(c:Coord) = Coord(if (c.x < 0.5 && c.x > -0.5) 0 else c.x, if (c.y < 1.0 && c.y > -1.0) 0 else c.y)

  def move() = {
    val vel = cutout((velocity*friction) + accelleration)

    val pos = position + vel
    Motion(accelleration, vel, pos, friction)
  }
}