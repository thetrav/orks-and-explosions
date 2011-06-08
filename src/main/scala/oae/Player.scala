package oae


import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.Color
import java.awt.geom._

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
  def size = animations(currentAnimation).size
  def width = size.x
  def height = size.y

  def init() {
    id = Physics.addEntity(Coord(0,0), size)
  }

  val runSpeed = 0.9
  val jumpPower = -30
  var jumpCounter = 0
  val jumpLimit = 60

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

    animations += currentAnimation -> animations(currentAnimation).update(math.abs(Physics.vel(id).x))

    mousePos = mouseWorldPos

    if(input.contains(KeyEvent.VK_D)) {
      Physics.dig(digShape)
    }
  }

  def digShape = {
    val top = y+(height/2)
    val bottom = y+50+(height/2)
    val playerFoot = y + height

    if(playerFacing == RIGHT) {
      val left = x + width
      val right = x + 100 + width
      Shape(List(
        Coord(left, top),
        Coord(right, top),
        Coord(right, bottom),
        Coord(left, playerFoot)
      ))
    } else {
      val left = x - 100
      val right = x
      Shape(List(
        Coord(left, top),
        Coord(right, top),
        Coord(right, playerFoot),
        Coord(left, bottom)
      ))
    }
  }

  def draw(g:Graphics2D) {
    g.translate(x, y)
    animations(currentAnimation).draw(g)
    g.translate(-x, -y)

    Debug.out("heading:"+playerFacing)
    g.setColor(new Color(255,0,0,100))
    g.fill(digShape.shape)

  }
}