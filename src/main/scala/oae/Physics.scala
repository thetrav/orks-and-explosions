package oae

import physics._
import java.awt._
import java.awt.geom._
import scala.collection.immutable.List



object Physics {
  val minimum = 0.1
  var id_counter = 0
  var entities = Map[Int, Entity]()

  var world = List(Segment(Coord(500,200), Coord(-500,200), Coord(0,-1)),
                   Segment(Coord(500,-20), Coord(-500,-20), Coord(0,1)),
                   Segment(Coord(500,-20), Coord(500,200), Coord(-1,0)),
                   Segment(Coord(-500,200), Coord(-500,-20), Coord(1,0)),
                   Segment(Coord(-500,150), Coord(-400,150), Coord(0,-1)),
                   Segment(Coord(400,200), Coord(450,150), Coord(-1,-1).normalize)
  )

  var collidedSegments = Map[Segment, Boolean]()


  def addEntity(pos:Coord, size:Double) = {
    id_counter += 1
    val id = id_counter
    entities += (id -> Entity(id, pos, size))
    id
  }

  def pos(id:Int)   = entities(id).pos
  def vel(id:Int)   = entities(id).vel
  def accel(id:Int) = entities(id).accel

  def addAccel(id:Int, accel:Coord) {
    entities += (id -> entities(id).addAccel(accel))
  }

  def allCollisions(motion:Segment, surfaces:List[Segment]) = {
    val list = for(s <- surfaces) yield {
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

  def closestCollision(motion:Segment, surfaces:List[Segment]) = {
    val potentialCollisions = allCollisions(motion, surfaces)
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
      val newPos = contact.intersect + (contact.surface.normal * minimum)

      //rotate velocity
      val angle = angleBetween(contact.surface, Segment(Coord(-1,0), Coord(1,0)))
      val rotated = entity.vel.rotate(angle)
      val flipped = rotated.copy(y = -1*rotated.y)
      val newVelocity = flipped.rotate(-angle)

      entity.copy(pos = newPos, vel = newVelocity)
  }

  def separate(entity:Entity, contact:Contact) = {
      val newPos = contact.intersect + (contact.surface.normal * minimum)

      entity.copy(pos = newPos)
  }

  def handleClosestCollisions(id:Int, lastFrame:Map[Int, Entity], projectedFrame:Map[Int, Entity]) = {
    val entity = projectedFrame(id)
    val oldEntity = lastFrame(id)
    val motion = Segment(oldEntity.pos, entity.pos)
    //find the closest potential intersect
    closestCollision(motion, world) match {
      case None => entity
      case Some(contact) => {
        collidedSegments += (contact.surface -> true)
        flipVelocityAndSeparate(entity, contact)
      }
    }
  }

  def handleRemainingCollisions(id:Int, lastFrame:Map[Int, Entity], projectedFrame:Map[Int, Entity]) = {
    var entity = projectedFrame(id)
    var motion = Segment(lastFrame(id).pos, entity.pos)
    if(motion.size > minimum) {
      val collisions = allCollisions(motion, world)
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
    world.foreach((segment:Segment) => {
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
      g.draw(new Ellipse2D.Double(e.x, e.y, 1, 1))
    })
  }
}