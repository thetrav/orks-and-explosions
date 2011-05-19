
import oae._
import oae.physics._
import Math.Pi

import org.specs.Specification

class TestMotion extends Specification {

  def difference(a:Double, b:Double) = {
    val dif = a - b
    if(dif == 0 ) 0 else math.sqrt(dif * dif)
  }

  def testSegments(a:Segment, b:Segment, expected:Double) = {
    val angle = Physics.angleBetween(a, b)
    println("angle,"+angle)
    val pass = difference(angle, expected) < 0.00005
    if(!pass) angle mustBe expected
    pass mustBe true
  }

  "Physics collisions" should {
    "calculate the correct angles" in {
      val floor = Segment(Coord(-10, 10), Coord(10, 10), Coord(0,-1))
      testSegments(floor, Segment(Coord(-5, 5), Coord(5,15)), (Pi / 4))
      testSegments(floor, Segment(Coord(-1, 5), Coord(1,5)), 0)
    }

    "allow the player to move along the surface of a line" in {
      println("posX,posY,accelX,accelY,velX,velY")
      val id = Physics.addEntity(Coord(0,150), 10)
      var counter = 50
      while(counter > 0) {
        counter -= 1
        println(Physics.pos(id).x+","+Physics.pos(id).y+","
          +Physics.accel(id).x+","+Physics.accel(id).y
          +","+Physics.vel(id).x+","+Physics.vel(id).y)
        Physics.simulate
        Physics.addAccel(id, Coord(1,0))
      }
      0
    }
  }
}