package oae

import physics._
import java.awt.geom._

trait SegmentState
case class InnerSegment(seg:Segment) extends SegmentState
case class OuterSegment(seg:Segment) extends SegmentState
case class SliceSegment(seg:Segment, hits:List[Contact]) extends SegmentState

object Shape {
  def singleSlice(segment:Segment, contact:Contact) = {
    val otherSeg = contact.surface
    if(segment.facingSameDirection(Segment(Coord(0,0),otherSeg.normal))) {
      Segment(segment.a, contact.intersect)
    } else {
      Segment(contact.intersect, segment.b)
    }
  }

  def slice(seg:Segment, other:Shape, hits:List[Contact], starts:Map[Coord, Segment]) = {
    var segmentStarts = starts
    if(hits.size == 1) {
      println("single intersect")
      val contact = hits.head
      val keeper = singleSlice(seg, contact)
      segmentStarts += (keeper.a -> keeper)
    } else if (hits.size == 2) {
      println("two intersects")
      val contact = hits.head
      val otherSeg = contact.surface
      if(seg.facingSameDirection(Segment(Coord(0,0), otherSeg.normal))) {
        //keep outer slices
        segmentStarts += (seg.a -> Segment(seg.a, contact.intersect))
        val secondContact = hits(1)
        val secondStart = secondContact.intersect
        segmentStarts += (secondStart -> Segment(secondStart, seg.b))
      } else {
       //keep inner slice
        val a = hits(0).intersect
        val b = hits(1).intersect
        segmentStarts += (a -> Segment(a, b))
      }
    } else {
      println("wtf, more than two?  You crazy players you broke shit! I HOPE YOU'RE HAPPY!")
    }
    segmentStarts
  }

  def process(a:Shape, b:Shape, starts:Map[Coord, Segment]) = {
    var segmentStarts = starts
    a.segments.foreach((seg:Segment) => {
      println("examining" + seg)
      segmentState(seg, b) match {
        case InnerSegment(_) => println("dropping")
        case OuterSegment(_) => segmentStarts += (seg.a -> seg)
        case SliceSegment(_, hits) => segmentStarts = slice(seg, b, hits, segmentStarts)
      }
    })
    segmentStarts
  }

  def segmentState(seg:Segment, shape:Shape):SegmentState = {
    val hits = shape.intersects(seg)
    if(hits.isEmpty) {
      val containsA = shape.containsCoord(seg.a)
      val containsB = shape.containsCoord(seg.b)
      if(containsA && containsB) {
        InnerSegment(seg)
      } else {
        OuterSegment(seg)
      }
    } else {
      SliceSegment(seg, hits)
    }
  }
}

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

    segmentStarts = Shape.process(this, other, segmentStarts)
    segmentStarts = Shape.process(other, this, segmentStarts)

    println("segmentStarts:"+segmentStarts)

    val startSegment = segmentStarts.head._2
    var pointList = List(startSegment.a)
    var nextSegment = segmentStarts(startSegment.b)
    while(nextSegment != startSegment) {
      nextSegment.a :: pointList
      nextSegment = segmentStarts(nextSegment.b)
    }
    Shape(pointList.reverse)
  }



  def intersects(seg:Segment) = {
    val hits = for( segment <- segments) yield {
      seg.intersect(segment) match {
        case Some(c) => Some(Contact(seg, segment, c))
        case None => None
      }
    }.toList
    hits.flatten.sort((a:Contact, b:Contact) => { a.distance < b.distance })
  }

  def containsCoord(coord:Coord) = {
    shape.contains(coord.x, coord.y)
  }

  def containsSegment(segment:Segment) = {
    containsCoord(segment.a) && containsCoord(segment.b)
  }
}