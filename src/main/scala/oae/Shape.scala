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
      Segment(segment.a.round, contact.intersect.round)
    } else {
      Segment(contact.intersect.round, segment.b.round)
    }
  }

  def slice(seg:Segment, other:Shape, hits:List[Contact], starts:Map[Coord, Segment]) = {
    var segmentStarts = starts
    if(hits.size == 1) {
      val contact = hits.head
      val keeper = singleSlice(seg, contact)
      segmentStarts += (keeper.a -> keeper)
    } else if (hits.size == 2) {
      val contact = hits.head
      val otherSeg = contact.surface
      if(seg.facingSameDirection(Segment(Coord(0,0), otherSeg.normal))) {
        segmentStarts += (seg.a -> Segment(seg.a.round, contact.intersect.round))
        val secondContact = hits(1)
        val secondStart = secondContact.intersect.round
        segmentStarts += (secondStart -> Segment(secondStart, seg.b))
      } else {
        val a = hits(0).intersect.round
        val b = hits(1).intersect.round
        segmentStarts += (a.round -> Segment(a, b))
      }
    } else {
      println("wtf, more than two?  You crazy players you broke shit! I HOPE YOU'RE HAPPY!")
      throw new RuntimeException
    }
    segmentStarts
  }

  def process(a:Shape, b:Shape, starts:Map[Coord, Segment]) = {
    var segmentStarts = starts
    a.segments.foreach((seg:Segment) => {
      segmentState(seg, b) match {
        case InnerSegment(_) => None
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

  def assembleShape(starts:Map[Coord, Segment]) = {
    val segmentStarts = starts.filter((keyVal:(Coord, Segment)) => {
      math.abs(keyVal._2.size) > 0
    })
    println("starts:"+segmentStarts)
    val startSegment = segmentStarts.head._2
    var pointList = List(startSegment.a)
    var nextSegment = segmentStarts(startSegment.b.round)
    while(!nextSegment.equals(startSegment)) {
      pointList = nextSegment.a :: pointList
      nextSegment = segmentStarts(nextSegment.b.round)
    }
    Shape(pointList.reverse)
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
    try {
      var segmentStarts = Map[Coord, Segment]()

      segmentStarts = Shape.process(this, other, segmentStarts)
      segmentStarts = Shape.process(other, this, segmentStarts)

      Shape.assembleShape(segmentStarts)
    } catch {
      case e => {
        e.printStackTrace
        println("dig failed\nworld:"+this+"\ndigHole:"+other)
        this
      }
    }

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