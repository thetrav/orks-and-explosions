package oae

import physics._
import java.awt._
import java.awt.geom._
import scala.collection.immutable.List

case class Segment(a:Coord, b:Coord, normal:Coord = Coord(0,0)) {
  def intersect(other:Segment) = {
    val x1 = a.x
    val y1 = a.y
    val x2 = b.x
    val y2 = b.y
    val x3 = other.a.x
    val y3 = other.a.y
    val x4 = other.b.x
    val y4 = other.b.y

    val denominator = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1)
    if(denominator == 0) {
      None
    } else {
      val numeratorA = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)
      val numeratorB = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)

      val unknownA = numeratorA / denominator
      val unknownB = numeratorB / denominator
      if((unknownA < 1 && unknownA > 0) && (unknownB < 1 && unknownB > 0)) {
        val x = x1 + unknownA * (x2 - x1)
        val y = y1 + unknownA * (y2 - y1)
//        println("collision\n\tmovement:"+this
//          +"\n\tfloor:"+other
//          +"\n\tdenominator:"+denominator
//          +"\n\tnumeratorA:"+numeratorA
//          +"\n\tnumeratorB:"+numeratorB
//          +"\n\tunknownA:"+unknownA
//          +"\n\tunknownB:"+unknownB
//          +"\n\tintersect:"+Coord(x,y))
        Some(Coord(x,y))
      } else{
        None
      }
    }
  }

  def vector() = b - a

  def size() = {
    math.sqrt(((a.x - b.x) * (a.x - b.x)) + ((a.y - b.y) * (a.y - b.y)))
  }

  def rotate(angle:Double) = Segment(a.rotate(angle), b.rotate(angle), normal.rotate(angle))
  def + (c:Coord) = Segment(a + c, b + c, normal + c)
  def - (c:Coord) = Segment(a - c, b - c, normal - c)
}

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

  def handleCollisions(id:Int, lastFrame:Map[Int, Entity], currentFrame:Map[Int, Entity]) = {
    val entity = currentFrame(id)
    val oldEntity = lastFrame(id)
    val motion = Segment(oldEntity.pos, entity.pos)
    val lossOnImpact = 0.6

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