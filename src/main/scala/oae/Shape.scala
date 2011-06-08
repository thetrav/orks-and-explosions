package oae

import physics._
import java.awt.geom._



case class Shape(points:List[Coord]) {
  def replace(source:Segment, target:List[Segment]) = {
    val replaced = for(segment <- segments) yield {
      if(segment == source) {
        target
      } else {
        List(segment)
      }
    }.toList
    val newPoints = replaced.flatten.map(_.b)
    Shape(newPoints)
  }

  def segments = {
    Segment(points.last, points.head) :: (for(p <- points.sliding(2)) yield Segment(p(0), p(1))).toList
  }

  def shape = {
    val path = new Path2D.Double
    path.moveTo(points.head.x, points.head.y)
    points.tail.foreach((p:Coord) => {path.lineTo(p.x, p.y)})
    path.lineTo(points.head.x, points.head.y)
    path
  }

  def merge(other:Shape) = {
    var segmentStarts = Map[Coord, Segment]()

    segments.foreach((seg:Segment) => {
      val containsA = other.contains(seg.a)
      val containsB = other.contains(seg.b)
      if(!containsA && !containsB) { //outer segment
        segmentStarts += (seg.a -> seg)
      } else if ((containsA && !containsB) || (!containsA && containsB)) {//slicing required
        segmentStarts = slice(seg, other, segmentStarts)
      } //else it's completely enclosed and should be dropped
    })
  }

  def slice(seg:Segment, other:Shape, segmentStarts:Map[Coord, Segment]) {
    val intersects = other.intersects(seg)

  }

  def intersects(seg:Segment) {
    val hits = for( segment <- segments) yield {
      seg.intersect(segment) match {
        Some(c) => Some(Contact(seg, segment, c))
        None => None
      }
    }.toList
    hits.flatten.sort((a:Contact, b:Contact) => { a.distance < b.distance })
  }

  def contains(coord:Coord) = {
    shape.contains(coord.x, coord.y)
  }

  def contains(segment:Segment) = {
    contains(segment.a) && contains(segment.b)
  }
}