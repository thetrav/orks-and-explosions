package oae


import java.awt.Graphics2D
import Images.dwarf
import java.awt.event.KeyEvent

object Player {
  var id = 0
  var animations = Map(
    "walk-left" -> Images.dragonWalkLeft,
    "walk-right" -> Images.dragonWalkRight,
    "stand" -> Images.dragonStand
  )
  var currentAnimation = "stand"

  def pos = Physics.pos(id)
  def x = pos.x
  def y = pos.y

  def init() {
    id = Physics.addEntity(Coord(0,0), animations(currentAnimation).size.asInstanceOf[Double])
  }

  val runSpeed = 1.0

  def simulate(input:Map[Int, Long]) {
    if(input.contains(KeyEvent.VK_RIGHT)) {
      Physics.addAccel(id, Coord(runSpeed,0))
      currentAnimation = "walk-right"
    } else if(input.contains(KeyEvent.VK_LEFT)) {
      Physics.addAccel(id, Coord(-runSpeed,0))
      currentAnimation = "walk-left"
    } else {
      currentAnimation = "stand"
    }

    animations += currentAnimation -> animations(currentAnimation).update(math.abs(Physics.vel(id).x))
  }

  def draw(g:Graphics2D) {
    g.translate(x, y)
    animations(currentAnimation).draw(g)
    g.translate(-x, -y)
  }
}