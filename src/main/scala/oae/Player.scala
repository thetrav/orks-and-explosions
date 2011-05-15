package oae

import java.awt.event.KeyEvent
import java.awt.Graphics2D
import oae.physics.Space

object Player {
  val walkPower = 0.5

  var physicsId = -1
  var currentAnimation = "stand"
  val animations = Map(
    "stand" -> Images.dwarfStand,
    "walk-left" -> Images.dwarfWalkLeft,
    "walk-right" -> Images.dwarfWalkRight
  )

  def coord = Space.pos(physicsId)

  def init() {
    val size = Coord(Images.dwarf.getWidth().asInstanceOf[Double],
      Images.dwarf.getHeight().asInstanceOf[Double])
    physicsId = Space.createEntity("Dwarf", Coord(0,100-size.y), size)

  }

  private def applyWalk(inputCode:Int, animation:String, xAccel:Double) {
    if(Main.input.contains(inputCode)) {
      if(currentAnimation != animation) {
        currentAnimation = animation
        Space.selfAccel(physicsId, Coord(xAccel, 0))
      }
    } else if(currentAnimation == animation) {
      currentAnimation = "stand"
      Space.selfAccel(physicsId, Coord(0, 0))
    }
  }

  def simulate() {
    applyWalk(KeyEvent.VK_LEFT, "walk-left", walkPower*(-1))
    applyWalk(KeyEvent.VK_RIGHT, "walk-right", walkPower)
  }

  def draw(g:Graphics2D) {
    val pos = Space.pos(physicsId)
    animations(currentAnimation).draw(g, pos)
  }
}