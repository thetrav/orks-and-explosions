package oae.physics

import oae._
import oae.geom.Segment
import oae.geom.Coord

case class Contact(motion:Segment, surface:Segment, intersect:Coord) {
  def distance = intersect.distance(motion.a)
}