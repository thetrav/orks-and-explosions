package oae.physics

import oae._

case class Entity(id:Int, pos:Coord, size:Double, accel:Coord = Coord(0,0), vel:Coord = Coord(0,0), friction:Double = 0.95) {
  def move = {
    this.copy(pos = pos + vel, vel = (vel + accel) * friction, accel = Coord(0,0))
  }

  def x = pos.x
  def y = pos.y

  def addAccel(amount:Coord) = {
    this.copy(accel = accel + amount)
  }
}