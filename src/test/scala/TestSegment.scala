
import oae._
import oae.physics._
import math.Pi

import org.specs.Specification

class TestSegment extends Specification {

  "Segment" should {
    "calculate correct normal" in {
      Segment(Coord(0,0), Coord(1,0)).normal must be equalTo(Coord(0,1))
    }

    "recognise same direction" in {
      val xAxis = Segment(Coord(0,0), Coord(1,0))
      Segment(Coord(0,0), Coord(1,0)) facingSameDirection(xAxis) must be equalTo true
      Segment(Coord(0,0), Coord(1,1)) facingSameDirection(xAxis) must be equalTo true
      Segment(Coord(0,0), Coord(1,-1)) facingSameDirection(xAxis) must be equalTo true
      Segment(Coord(0,0), Coord(-1,1)) facingSameDirection(xAxis) must be equalTo false
      Segment(Coord(0,0), Coord(-1,-1)) facingSameDirection(xAxis) must be equalTo false
      Segment(Coord(0,0), Coord(0,1)) facingSameDirection(xAxis) must be equalTo false
      Segment(Coord(0,0), Coord(0,-1)) facingSameDirection(xAxis) must be equalTo false
      Segment(Coord(0,0), Coord(0,0)) facingSameDirection(xAxis) must be equalTo false

      val seg = Segment(Coord(0,0), Coord(1,0))
      seg.normal must be equalTo(Coord(0,1))
      val segNorm = Segment(Coord(0,0), seg.normal)
      Segment(Coord(0.5,-1), Coord(0.5, 1)) facingSameDirection(segNorm) must be equalTo true
      Segment(Coord(0.5,1), Coord(0.5, -1)) facingSameDirection(segNorm) must be equalTo false
    }
  }


}