package oae.physics

import oae._
import oae.geom.Coord

case class Entity(id:Int, pos:Coord, size:Coord, accel:Coord = Coord(0,0), vel:Coord = Coord(0,0), friction:Double = 0.85) {
  val gravity = Coord(0, 0.5)
  def move = {
    val newAccel = accel + gravity
    val newVel = (vel + newAccel) * friction
    val newPos = pos + newVel
    this.copy(pos = newPos, vel = newVel, accel = Coord(0,0))
  }

  def x = pos.x
  def y = pos.y

  def addAccel(amount:Coord) = {
    this.copy(accel = accel + amount)
  }

  def topLeft = pos
  def topRight = pos + Coord(size.x, 0)
  def bottomRight = pos + size
  def bottomLeft = pos + Coord(0, size.y)
}