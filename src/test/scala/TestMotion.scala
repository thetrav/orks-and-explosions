
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

    "correctly determine the closest collision" in {
      val closest = Segment(Coord(-10,5), Coord(10, 5), Coord(0,-1))
      val surfaces = List(
        Segment(Coord(-10,0), Coord(10, 0), Coord(0,-1)),
        closest,
        Segment(Coord(-10,10), Coord(10, 10), Coord(0,-1)),
        Segment(Coord(-10,15), Coord(10, 15), Coord(0,-1))
      )
      val motion = Segment(Coord(0, 3), Coord(0,12))

      val contact = Physics.closestCollision(motion, surfaces)
      (contact == None) mustBe false
      contact.get.surface mustBe closest
    }

    "correctly detect collision on a horizontal plane moving down" in {
      var world = List(Segment(Coord(1005,200), Coord(-1005,200), Coord(0,-1)))
      val motion = Segment(Coord(207.30465946760884,195.7442237877346),
                           Coord(221.29777767275974,201.28054765086702))
      val contact = Physics.closestCollision(motion, world)
      contact.isDefined mustBe true
    }

    "correctly detect collision on a vertical plane moving left" in {
      var world = List(Segment(Coord(10,-10), Coord(10,10), Coord(1,0)))
      val motion = Segment(Coord(15,0),
                           Coord(5,0))
      val contact = Physics.closestCollision(motion, world)
      contact.isDefined mustBe true
    }

    "correctly ignore collision on a vertical plane moving with the normal" in {
      var world = List(Segment(Coord(10,-10), Coord(10,10), Coord(1,0)))
      val motion = Segment(Coord(5,0),
                           Coord(15,0))
      val contact = Physics.closestCollision(motion, world)
      contact.isDefined mustBe false
    }

    "correctly detect collision on an angled plane" in {
      var world = List(Segment(Coord(-10,-10), Coord(10,10), Coord(-1,1).normalize))
      val motion = Segment(Coord(-5,5),
                           Coord(5,-5))
      val contact = Physics.closestCollision(motion, world)
      contact.isDefined mustBe true
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