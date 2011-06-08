
import oae._
import oae.physics._

import org.specs.Specification

class TestSlice extends Specification {
  "SingleSlice" should {
    "keep first segment of a slice going against the normal" in {
      val segment = Segment(Coord(0.5,-1), Coord(0.5, 1))
      val surface = Segment(Coord(0,0), Coord(1,0))

      val contact = Contact(segment, surface, Coord(0.5, 0))

      val slice = Shape.singleSlice(segment, contact)

      slice must be equalTo(Segment(Coord(0.5, -1), Coord(0.5, 0)))
    }

    "keep second segment of a slice going with the normal" in {
      val segment = Segment(Coord(0.5, 1), Coord(0.5,-1))
      val surface = Segment(Coord(0,0), Coord(1,0))

      val contact = Contact(segment, surface, Coord(0.5, 0))

      val slice = Shape.singleSlice(segment, contact)

      slice must be equalTo(Segment(Coord(0.5, 0), Coord(0.5, -1)))
    }
  }

  "segmentState" should {
    "detect outer semgent" in {
      val shape = Shape(List(Coord(200, 100), Coord(220, 100), Coord(220,120)))
      val seg = Segment(Coord(10,10), Coord(210,10))
      Shape.segmentState(seg, shape) must be equalTo(OuterSegment(seg))
    }

    "detect inner semgent" in {
      val world = Shape(List(Coord(10,10), Coord(210,10), Coord(210,210), Coord(10,210)))
      val seg = Segment(Coord(20,20), Coord(30,30))
      Shape.segmentState(seg, world) must be equalTo(InnerSegment(seg))
    }

    "detect slice semgent" in {
      val world = Shape(List(Coord(10,10), Coord(210,10), Coord(210,210), Coord(10,210)))
      val seg = Segment(Coord(200, 100), Coord(220, 100))
      val expectedContact = Contact(seg, Segment(Coord(210,10), Coord(210,210)), Coord(210,100))
      Shape.segmentState(seg, world) must be equalTo(SliceSegment(seg, List(expectedContact)))
    }
  }
}