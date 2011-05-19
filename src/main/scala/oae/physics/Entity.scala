package oae.physics

import oae._

case class Entity(id:Int, pos:Coord, size:Double, accel:Coord = Coord(0,0), vel:Coord = Coord(0,0), friction:Double = 0.95) {
  val gravity = Coord(0, 0.9)
  def move = {
    val newAccel = accel + gravity
    val newVel = (vel + newAccel) * friction
    val newPos = pos + newVel
//    println("moving from "+pos+" to "+newPos)
    this.copy(pos = newPos, vel = newVel, accel = Coord(0,0))
  }

  def x = pos.x
  def y = pos.y

  def addAccel(amount:Coord) = {
    this.copy(accel = accel + amount)
  }
}