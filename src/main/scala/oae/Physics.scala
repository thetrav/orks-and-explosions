package oae

import physics._
import java.awt._
import java.awt.geom._
import scala.collection.immutable.List



object Physics {
  var id_counter = 0
  var entities = Map[Int, Entity]()

  var world = List(Segment(Coord(105,200), Coord(-105,200), Coord(0,-1)),
                   Segment(Coord(205,-20), Coord(-205,-20), Coord(0,-1)),
                   Segment(Coord(200,-25), Coord(100,205), Coord(0,-1)),
                   Segment(Coord(-100,205), Coord(-200,-25), Coord(0,-1))
  )


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

  def closestCollision(motion:Segment, surfaces:List[Segment]):Option[Contact] = {
    val potentialCollisions = for(s <- surfaces) yield {
      if(motion.vector.dot(s.normal) < 0) {
        motion.intersect(s) match {
          case Some(c) => Some((s, c.distance(motion.a), c))
          case None    => None
        }
      } else {
        None //not moving towards the surface, we don't care if it collided
      }
  }.toList
    if(potentialCollisions.flatten.isEmpty) {
      None
    } else {
      val closest = potentialCollisions.flatten.reduceLeft {
        (collect, current) => {
          if(current._2 < collect._2) current else collect
        }
      }
      Some(Contact(motion, closest._1, closest._3))
    }
  }

  def handleCollisions(id:Int, lastFrame:Map[Int, Entity], currentFrame:Map[Int, Entity]) = {
    val entity = currentFrame(id)
    val oldEntity = lastFrame(id)
    val motion = Segment(oldEntity.pos, entity.pos)
    //find the closest potential intersect
    closestCollision(motion, world) match {
      case None => entity
      case Some(contact) => {
        val newPos = contact.intersect - entity.vel.normalize

        //rotate velocity
        val angle = angleBetween(contact.surface, Segment(Coord(-1,0), Coord(1,0)))
        val rotated = entity.vel.rotate(angle)
        val flipped = rotated.copy(y = -1*rotated.y)
        val newVelocity = flipped.rotate(-angle)

        entity.copy(pos = newPos, vel = newVelocity)
      }
    }
  }

  def angleBetween(a:Segment, b:Segment) = {
    val aVector = a.vector
    val bVector = b.vector
    if(aVector == Coord(0,0) || bVector == Coord(0,0)) { 0 } else {
      math.acos(aVector.dot(bVector) / (aVector.size * bVector.size()))
    }
  }

  def simulate {
    val newEntities = for(t:(Int, Entity) <- entities) yield {
      (t._1, t._2.move)
    }
    val shiftedEntities = for(t:(Int, Entity) <- entities) yield {
      val id = t._1

      id -> handleCollisions(id, entities, newEntities)
    }
    entities = shiftedEntities
  }

  def int(d:Double) = d.asInstanceOf[Int]
  def draw(g:Graphics2D) {
    g.setColor(Color.green)
    world.foreach((segment:Segment) => {
      g.drawLine(int(segment.a.x), int(segment.a.y),
                 int(segment.b.x), int(segment.b.y))
    })

    entities.foreach((tuple:(Int, Entity)) => {
      val e = tuple._2
      g.draw(new Ellipse2D.Double(e.x, e.y, e.size, e.size))
    })
  }
}