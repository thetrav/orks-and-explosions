package oae


import inventory.Item
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.Color
import collection.Map

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

  val LEFT = Coord(-1,0)
  val RIGHT = Coord(1,0)
  var playerFacing = LEFT

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
  var digDirection:Direction = Middle
  var inventory = Map("stone" -> 0)

  def increaseInvCount(item:String) {
    inventory += item -> (inventory(item) + 1)
  }

  def simulate(input:Map[Int, Long], mouseWorldPos:Coord) {
    if(input.contains(KeyEvent.VK_RIGHT)) {
      Physics.addAccel(id, Coord(runSpeed,0))
      currentAnimation = "walk-right"
      playerFacing = RIGHT

    } else if(input.contains(KeyEvent.VK_LEFT)) {
      Physics.addAccel(id, Coord(-runSpeed,0))
      currentAnimation = "walk-left"
      playerFacing = LEFT
    } else {
      animations += currentAnimation -> animations(currentAnimation).reset()
    }

    if(input.contains(KeyEvent.VK_SPACE) && jumpCounter > jumpLimit) {
      Physics.addAccel(id, Coord(0, jumpPower))
      jumpCounter = 0
    }
    jumpCounter += 1

    if (input.contains(KeyEvent.VK_DOWN)) {
      digDirection = Down
    } else if (input.contains(KeyEvent.VK_UP)) {
      digDirection = Up
    } else {
      digDirection = Middle
    }

    if(input.contains(KeyEvent.VK_D)) {
      digDown = true
    } else {
      if (digDown) {
        Physics.dig(digPoint) match {
          case Some(item) => increaseInvCount(item)
          case None => ""
        }
      }
      digDown = false
    }

    animations += currentAnimation -> animations(currentAnimation).update(math.abs(Physics.vel(id).x))

    mousePos = mouseWorldPos
  }

  def yOffset = digDirection match {
    case Up => -gridSize
    case Down => gridSize
    case _ => 0
  }

  def digPoint = {
    val dy = y + gridSize - (y % gridSize) + yOffset
    val dx = if(currentAnimation == "walk-right") {
      x + width + gridSize - (x % gridSize)
    } else {
      x - gridSize - (x % gridSize)
    }
    Coord(dx,dy)
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

  def drawInventory(g:Graphics2D) {
    inventory.map ((pair) => {
      val img = Images.img("inventory/"+pair._1)
      g.drawImage(img, null, 10, 10)
      g.setColor(Color.white)
      g.drawString("x "+pair._2, 45, 25)
    })
  }
}