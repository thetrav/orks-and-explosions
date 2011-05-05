package oae


import java.awt.Graphics2D
import Images.dwarf
import oae.physics.Motion

case class Dwarf(motion:Motion, currentAnimation:String, animations:Map[String, Animation]) {

  def accell(delta:Coord) = Dwarf(
    motion.accell(delta),
    currentAnimation,
    animations
  )

  def simulate() = {
    val newMotion = motion.move()
    val velX = newMotion.velocity.x

    val newAnimation = if(velX < 0) "walk-left" else if (velX == 0) currentAnimation else "walk-right"
    val speed = if(velX < 0) velX * (-1) else velX

    val newAnimations = animations + (newAnimation -> animations(newAnimation).update(speed*0.2))

    Dwarf(
      newMotion,
      newAnimation,
      newAnimations
    )
  }

  def x = motion.position.x
  def y = motion.position.y

  def draw(g:Graphics2D) {
    animations(currentAnimation).draw(g, motion.position)
  }
}