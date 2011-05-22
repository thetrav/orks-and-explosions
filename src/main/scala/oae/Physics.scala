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
          case Some(c) => Some((s, c.distance(motion.a)))
          case None    => None
        }
      } else {
        None //not moving towards the surface, we don't care if it collided
      }
  }.toList
    if(potentialCollisions.flatten.isEmpty) {
      None
    } else {
      val surface = potentialCollisions.flatten.reduceLeft {
        (collect, current) => {
          if(current._2 < collect._2) current else collect
        }
      }._1
      Some(Contact(motion, surface))
    }
  }

  def handleCollisions(id:Int, lastFrame:Map[Int, Entity], currentFrame:Map[Int, Entity]) = {
    val entity = currentFrame(id)
    val oldEntity = lastFrame(id)
    val motion = Segment(oldEntity.pos, entity.pos)
    val lossOnImpact = 0.6

    //find the closest potential intersect
    val contact = closestCollision(motion, world)
    //work out the bounce velocity


    //remove the motion after the bounce

    val collisions = for (surface <- world) yield {
      motion.intersect(surface) match {
        case None => entity
        case Some(c:Coord) => {
          def perform(segment:Segment, motion:Segment, intersect:Coord) = {
            //end pos = flip end coord.y
            val endPos = Coord(motion.b.x, -1 * motion.b.y)
            //newVel == motion.flip y
            val motionVector = motion.vector
            val newVel = motionVector.copy(y=motionVector.y * -1)
  //          println("oldVel,"+motionVector+"\nnewVel,"+newVel)
            (endPos, newVel)
          }
  
          def performWithRotation(angle:Double, segment:Segment, motion:Segment, intersect:Coord) = {
            val rotatedSegment = segment.rotate(angle)
            val rotatedMotion = motion.rotate(angle)
            val rotatedIntersect = intersect.rotate(angle)
            val translation = Coord(0, -1 * rotatedIntersect.y)
  
            val tuple = perform(rotatedSegment   + translation,
                                rotatedMotion    + translation,
                                rotatedIntersect + translation)
  //          println("tuple,"+tuple)
            val unTranslated = (tuple._1 - translation, tuple._2)
  //          println("untranslated"+unTranslated)
            val unRotated = (unTranslated._1.rotate(-1 * angle), unTranslated._2.rotate(-1 * angle))
  //          println("unrotated"+unRotated)
            unRotated
          }
  
          if(motion.vector.dot(surface.normal) < 0) { //ensures motion is towards the segment rather than away (stops double collisions)
            val angleOfNormal = angleBetween(surface, Segment(Coord(-1,0), Coord(1,0)))
  
            val tuple = performWithRotation(angleOfNormal, surface, motion, c)
            val newPos = tuple._1
            val newVelocity = tuple._2
  
            entity.copy(pos = newPos, vel = newVelocity)
          } else {
            entity
          }
        }
      }
    }

    //taking the first collision is a really crap way of solving the Time Of Impact problem
    collisions.find(_ != entity) match {
      case None => entity
      case Some(e) => e
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