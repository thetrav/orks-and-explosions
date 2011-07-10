package oae

import physics._
import java.awt._
import java.awt.geom._
import scala.collection.immutable.List

object Physics {
  val minimum = 0.1
  var id_counter = 0
  var entities = Map[Int, Entity]()
  def buildWorld = {
    val right = 1980
    val left = -180
    val top = -30
    val bottom = 180

    Shape(List(
      Coord(left,bottom),
      Coord(left,top),
      Coord(right,top),
      Coord(right,bottom)
    ))
  }

  val world = buildWorld

  var collidedSegments = Map[Segment, Boolean]()


  def addEntity(pos:Coord, size:Coord) = {
    id_counter += 1
    val id = id_counter
    entities += (id -> Entity(id, pos, size))
    id
  }

  def pos(id:Int)   = entities(id).pos
  def vel(id:Int)   = entities(id).vel
  def accel(id:Int) = entities(id).accel
  def size(id:Int) = entities(id).size

  def addAccel(id:Int, accel:Coord) {
    entities += (id -> entities(id).addAccel(accel))
  }

  def allCollisions(motions:List[Segment], surfaces:List[Segment]) = {
    val list = for(motion <- motions; s <- surfaces) yield {
      if(motion.vector.dot(s.normal) < 0) {
        motion.intersect(s) match {
          case Some(c) => Some(Contact(motion, s, c))
          case None    => None
        }
      } else {
        None //not moving towards the surface, we don't care if it collided
      }
    }.toList
    list.flatten
  }

  def closestCollision(motions:List[Segment], surfaces:List[Segment]) = {
    val potentialCollisions = allCollisions(motions, surfaces)
    if(potentialCollisions.isEmpty) {
      None
    } else {
      val closest = potentialCollisions.reduceLeft {
        (collect, current) => {
          if(current.distance < collect.distance) current else collect
        }
      }
      Some(closest)
    }
  }

  def flipVelocityAndSeparate(entity:Entity, contact:Contact) = {
      //rotate velocity
      val angle = angleBetween(contact.surface, Segment(Coord(-1,0), Coord(1,0)))
      val rotated = entity.vel.rotate(angle)
      val flipped = rotated.copy(y = -1*rotated.y)
      val newVelocity = flipped.rotate(-angle)

      separate(entity.copy(vel = newVelocity), contact)
  }

  def separate(entity:Entity, contact:Contact) = {
      val newPos = contact.intersect + (contact.surface.normal * minimum) +  entity.pos.offsetFrom(contact.motion.b)

      entity.copy(pos = newPos)
  }
  
  def buildMotions(lastFrame:Entity, projectedFrame:Entity) = {
    List(
      Segment(lastFrame.topLeft, projectedFrame.topLeft),
      Segment(lastFrame.topRight, projectedFrame.topRight),
      Segment(lastFrame.bottomRight, projectedFrame.bottomRight),
      Segment(lastFrame.bottomLeft, projectedFrame.bottomLeft)
    )
  }

  def handleClosestCollisions(id:Int, lastFrame:Map[Int, Entity], projectedFrame:Map[Int, Entity]) = {
    val entity = projectedFrame(id)
    val oldEntity = lastFrame(id)
    val motion = buildMotions(oldEntity, entity)

    //find the closest potential intersect
    closestCollision(motion, world.segments) match {
      case None => entity
      case Some(contact) => {
        collidedSegments += (contact.surface -> true)
        flipVelocityAndSeparate(entity, contact)
      }
    }
  }

  def handleRemainingCollisions(id:Int, lastFrame:Map[Int, Entity], projectedFrame:Map[Int, Entity]) = {
    var entity = projectedFrame(id)
    var motions = buildMotions(lastFrame(id), entity)
    if(motions.head.size > minimum) {
      val collisions = allCollisions(motions, world.segments)
      collisions.foreach((contact:Contact) => {
        collidedSegments += (contact.surface -> true)
        entity = separate(entity, contact)
      })
    }
    entity
  }

  def angleBetween(a:Segment, b:Segment) = {
    val aVector = a.vector
    val bVector = b.vector
    if(aVector == Coord(0,0) || bVector == Coord(0,0)) { 0 } else {
      math.acos(aVector.dot(bVector) / (aVector.size * bVector.size()))
    }
  }

  def simulate {
    collidedSegments = Map[Segment, Boolean]()

    val newEntities = for(t:(Int, Entity) <- entities) yield {
      (t._1, t._2.move)
    }
    val shiftedEntities = for(t:(Int, Entity) <- entities) yield {
      val id = t._1

      id -> handleClosestCollisions(id, entities, newEntities)
    }
    val solvedPositions = for(t:(Int, Entity) <- entities) yield {
      val id = t._1

      id -> handleRemainingCollisions(id, entities, shiftedEntities)
    }
    entities = shiftedEntities
  }

  def int(d:Double) = d.asInstanceOf[Int]
  def draw(g:Graphics2D) {
    world.segments.foreach((segment:Segment) => {
        g.setColor(if(collidedSegments.contains(segment)) Color.red else Color.green)
        g.drawLine(int(segment.a.x), int(segment.a.y),
                   int(segment.b.x), int(segment.b.y))
        g.setColor(Color.white)
        val n = (segment.normal * 40) + segment.a
        g.drawLine(int(segment.a.x), int(segment.a.y),
                   int(n.x), int(n.y))
    })

    entities.foreach((tuple:(Int, Entity)) => {
      val e = tuple._2
      val drawDot = (c:Coord) => {g.draw(new Ellipse2D.Double(c.x, c.y, 2, 2))}
      drawDot(e.topLeft)
      drawDot(e.topRight)
      drawDot(e.bottomRight)
      drawDot(e.bottomLeft)
    })
  }

  def dig(digPoint:Coord) {
//    world = world.dig(digPoint)
  }
}