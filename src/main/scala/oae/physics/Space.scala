package oae.physics

import oae._
import java.awt.Graphics2D
import java.awt.geom._
import java.awt.Color
import java.lang.Math

//for my physics,
// I have players, monsters and scenery
// scenery is not lethal, if players or monsters collide with it
  //  I just want them to not tunnel into it.  Maybe bounce a little if they collide at a high speed
// If they collide with each other, I want them to bounce off, and I want to know after the simulation


// objects ask the space to create their "physics representation" and are returned a key.
// objects can ask the space for information about their representation using the key
// the space is responsible for performing updates to these representations

case class Entity (
  id:Int,
  entityType:String,
  position:Coord,
  accelleration:Coord,
  selfPropulsion:Coord,
  velocity:Coord,
  size:Coord,
  rail:Option[Segment] = None) {

  val friction = 0.9

  // if I'm jumping or on a horizontal rail I can propel myself, 
  // but if I'm on a slope I should follow the slope
  def translate(dir:Coord) = {
    val x = dir.x
    val y = dir.y
    if(y > 0 || rail == None || x == 0) {
      dir
    } else {
      rail match {
        case Some(x:LeftRampSegment) => Coord(dir.x/2, dir.x/(-2))
        case Some(x:RightRampSegment) => Coord(dir.x/2, dir.x/2)
        case _ => dir 
      }
    }
  }

  def move() = {
    val accel = translate(selfPropulsion) + accelleration
    val newVel = ((velocity*friction) + accel).cutOffAt(Space.MIN_VELOCITY)

    val newPos = position + newVel
    copy(position = newPos, velocity = newVel)
  }

  def boundingBox = List(
    position,
    position + Coord(size.x, 0),
    position + size,
    position + Coord(0, size.y)
  )

  def centerCoord = (size*0.5)+position
};

trait Segment {
  def a:Coord
  def b:Coord

  def intersect(other:Segment) = {
    val x1 = a.x
    val x2 = b.x
    val x3 = other.a.x
    val x4 = other.b.x
    val y1 = a.y
    val y2 = b.y
    val y3 = other.a.y
    val y4 = other.b.y

    val denominator = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1)
    val uaNumerator   = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)
    val ubNumerator   = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)
    if(denominator == 0) {
      if(uaNumerator == 0 && ubNumerator == 0) {
        //coincident lines, in the case of the character
        // it will happen when he's sliding along or down an edge,
        // probably don't need a collision
        None
      } else {
        None
      }
    } else {

      val ua = uaNumerator / denominator
      val ub = ubNumerator / denominator

      if (ua > 0 && ua < 1 && ub > 0 && ub < 1) {
        //collision!
        val x = x1 + ua * (x2 - x1)
        val y = y1 + ua * (y2 - y1)
        Some(Coord(x, y))
      } else {
        None
      }
    }
  }
}

case class ArbitrarySegment(a:Coord, b:Coord) extends Segment

case class HorizontalSegment(x1:Double, y:Double, x2:Double) extends Segment {
  def a = Coord(x1, y)
  def b = Coord(x2, y)
}

case class VerticalSegment(x:Double, y1:Double, y2:Double) extends Segment {
  def a = Coord(x, y1)
  def b = Coord(x, y2)
}

case class LeftRampSegment(x1:Double, y1:Double, x2:Double, y2:Double) extends Segment {
  def a = Coord(x1, y1)
  def b = Coord(x2, y2)
}

case class RightRampSegment(x1:Double, y1:Double, x2:Double, y2:Double) extends Segment {
  def a = Coord(x1, y1)
  def b = Coord(x2, y2)
}

object Space {

  var MIN_VELOCITY = 0.005



  private var ids = 0
  private var entities:Map[Int, Entity] = Map()
  private val cave = List(
    HorizontalSegment(-100, 100, 100),
    RightRampSegment(-100, 100, -200, 100),
    VerticalSegment(-200, 0, -100)
//    HorizontalSegment(-200, 100, 200),
//    VerticalSegment(200, 200, 100)
    //    LeftRampSegment(-150, -60, -200, -10),
    //    HorizontalSegment(-200, -10, -400),
//    HorizontalSegment(-200, 100, 100),
//    VerticalSegment(100, 100, -10)
  )

  private def cavePath = {
    val path = new Path2D.Double()
    val head = cave.head
    path.moveTo(head.a.x, head.a.y)
    path.lineTo(head.b.x,  head.b.y)
    cave.tail.foreach((c:Segment) => {path.lineTo(c.b.x, c.b.y)})
    path.lineTo(head.a.x, head.a.y)
    path
  }


  def createEntity(entityType:String, position:Coord, size:Coord) = {
    val id = ids
    ids += 1
    entities = entities + (id -> Entity(
      id,
      entityType,
      position,
      Coord(0,0),
      Coord(0,0),
      Coord(0,0),
      size
    ))
    id
  }

  def selfAccel(id:Int, newAccel:Coord) {
    val entity = entities(id)
    entities = entities + (id -> entity.copy(selfPropulsion = newAccel))
  }

  def pos(id:Int) = entities(id).position

  def str(a:Array[Double]) = {
    a(0) +","+ a(1)
  }

  def handleCollision(e:Entity, lastFrame:Map[Int, Entity], movement:Segment, wall:Segment) = {
    val intersection = movement.intersect(wall).get

    e.copy(position = intersection, rail = Some(wall))

  }

  def keepInWalls(e:Entity, lastFrame:Map[Int, Entity]) = {
    if(allPointsInCave(e.boundingBox)) {
      e
    } else {
      val movement = ArbitrarySegment(lastFrame(e.id).centerCoord, e.centerCoord)
      val collided = cave.find((s:Segment) => s.intersect(movement) != None)
      collided match {
        case Some(x) => handleCollision(e, lastFrame, movement, x)
        case None => e
      }
    }
  }

  def allPointsInCave(points:List[Coord]) = {
    points.filter((p:Coord) => !cavePath.contains(p.x, p.y)).size == 0
  }

  def simulate() {
    val lastFrame = entities
    entities = for( tuple <- entities) yield {
      (tuple._1, tuple._2.move())
    }
    //keep within walls
    entities = for( tuple <- entities) yield {
      (tuple._1, keepInWalls(tuple._2, lastFrame))
    }
  }

  def int(d:Double) = d.asInstanceOf[Int]

  def draw(g:Graphics2D) {
    g.setColor(Color.green)
    g.draw(cavePath)
    entities.foreach((t:(Int,Entity)) => {
      val e = t._2
      g.drawRect(int(e.position.x), int(e.position.y), int(e.size.x), int(e.size.y))})
  }
}

