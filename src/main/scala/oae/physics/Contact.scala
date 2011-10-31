package oae.physics

import oae._

case class Contact(motion:Segment, surface:Segment, intersect:Coord) {
  def distance = intersect.distance(motion.a)
}