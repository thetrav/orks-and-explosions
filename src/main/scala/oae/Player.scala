package oae


import java.awt.Graphics2D
import java.awt.event.KeyEvent

object Player {
  var id = 0
  var animations = Map(
    "walk-left" -> Images.dwarfWalkLeft,
    "walk-right" -> Images.dwarfWalkRight,
    "stand" -> Images.dwarfStand
  )
  var currentAnimation = "stand"

  def pos = Physics.pos(id)
  def x = pos.x
  def y = pos.y

  def init() {
    id = Physics.addEntity(Coord(0,0), animations(currentAnimation).size)
  }

  val runSpeed = 0.9
  val jumpPower = -30
  var jumpCounter = 0
  val jumpLimit = 60

  def simulate(input:Map[Int, Long]) {
    if(input.contains(KeyEvent.VK_RIGHT)) {
      Physics.addAccel(id, Coord(runSpeed,0))
      currentAnimation = "walk-right"
    } else if(input.contains(KeyEvent.VK_LEFT)) {
      Physics.addAccel(id, Coord(-runSpeed,0))
      currentAnimation = "walk-left"
    } else {
      animations += currentAnimation -> animations(currentAnimation).reset()
    }

    if(input.contains(KeyEvent.VK_SPACE) && jumpCounter > jumpLimit) {
      Physics.addAccel(id, Coord(0, jumpPower))
      jumpCounter = 0
    }
    jumpCounter += 1

    animations += currentAnimation -> animations(currentAnimation).update(math.abs(Physics.vel(id).x))
  }

  def draw(g:Graphics2D) {
    g.translate(x, y)
    animations(currentAnimation).draw(g)
    g.translate(-x, -y)
  }
}