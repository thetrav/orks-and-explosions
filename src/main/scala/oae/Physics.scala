package oae

import physics._
import java.awt._
import java.awt.geom._

object Physics {
  var id_counter = 0
  var entities = Map[Int, Entity]()

  def addEntity(pos:Coord, size:Double) = {
    id_counter += 1
    val id = id_counter
    entities += (id -> Entity(id, pos, size))
    id
  }

  def pos(id:Int) = entities(id).pos
  def vel(id:Int) = entities(id).vel

  def addAccel(id:Int, accel:Coord) {
    entities += (id -> entities(id).addAccel(accel))
  }

  def simulate {
    entities = for(t:(Int, Entity) <- entities) yield {
      (t._1, t._2.move)
    }
  }


  def draw(g:Graphics2D) {
    g.setColor(Color.green)
    entities.foreach((tuple:(Int, Entity)) => {
      val e = tuple._2
      g.draw(new Ellipse2D.Double(e.x, e.y, e.size, e.size))
    })
  }
}