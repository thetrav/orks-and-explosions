package oae


import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.Color

import Game.gridSize

object Player {
  var mousePos = Coord(0,0)

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
  def width = Physics.size(id).x

  def init() {
    id = Physics.addEntity(Coord(30,80), animations(currentAnimation).size)
  }

  val runSpeed = 0.9
  val jumpPower = -30
  var jumpCounter = 0
  val jumpLimit = 60
  var digDown = false

  def simulate(input:Map[Int, Long], mouseWorldPos:Coord) {
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

    if(input.contains(KeyEvent.VK_D)) {
      digDown = true
    } else {
      if (digDown) {
        Physics.dig(digPoint)
      }
      digDown = false
    }

    animations += currentAnimation -> animations(currentAnimation).update(math.abs(Physics.vel(id).x))

    mousePos = mouseWorldPos
  }

  def digPoint = {
    if(currentAnimation == "walk-right") {
      Coord(x + width + gridSize - (x % gridSize), y + 2*gridSize - (y % gridSize))
    } else {
      Coord(x - gridSize - (x % gridSize), y + 2*gridSize - (y % gridSize))
    }
  }

  def draw(g:Graphics2D) {
    g.translate(x, y)
    animations(currentAnimation).draw(g)
    g.translate(-x, -y)

    g.setColor(Color.red)
    val dp = digPoint
    val ox = dp.x.asInstanceOf[Int] - 1
    val oy = dp.y.asInstanceOf[Int] - 1
    g.fillOval(ox, oy, 3, 3)


  }
}