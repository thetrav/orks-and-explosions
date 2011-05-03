package oae.physics

import oae.Coord

case class Motion(accelleration:Coord, velocity:Coord, position:Coord) {
  val friction = 0.85

  def accell(newValue:Coord) = Motion(newValue, velocity, position)

  def cutout(c:Coord) = Coord(if (c.x < 1.0 && c.x > -1.0) 0 else c.x, if (c.y < 1.0 && c.y > -1.0) 0 else c.y)

  def move() = {
    val vel = cutout((velocity*friction) + accelleration)

    val pos = position + vel
    Motion(accelleration, vel, pos)
  }
}