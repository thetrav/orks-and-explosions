package oae

import physics._
import Game.gridSize
import java.awt.geom._

case class Shape(points:List[Coord]) {

  val full_offsets = List(( -gridSize , -gridSize),
                          (        0  , -gridSize),
                          ( gridSize  , -gridSize),
                          (-gridSize  , 0),
                          (        0  , 0),
                          ( gridSize  , 0),
                          (-gridSize  , gridSize),
                          (        0  , gridSize),
                          ( gridSize  , gridSize))
  val half_offsets = List(
                      (-gridSize/2,-gridSize/2),
                      ( gridSize/2,-gridSize/2),
                      (-gridSize/2, gridSize/2),
                      ( gridSize/2, gridSize/2)
  )

  //clockwise is for normals and for merging back
  case class Chunk(grid:Map[Coord, Boolean], centerPos:Coord, clockwise:Boolean) {
    def apply(newGrid:Map[Coord, Boolean]) = Chunk(newGrid, centerPos, clockwise:Boolean)

    def shoulders = half_offsets.map((t:(Int, Int)) => centerPos + Coord(t._1, t._2))

    def printGrid {
      def f(c:Coord) = if(grid(c)) "x" else "o"
      println("grid:\n"+
        f(topLeft) + " " + f(top) + " " + f(topRight) + "\n" +
        " " + f(shoulders(0)) + " " + f(shoulders(1)) + "\n" +
        f(left) + " " + f(centerPos) + " " + f(right) + "\n" +
        " " + f(shoulders(2)) + " " + f(shoulders(3)) + "\n" +
        f(bottomLeft) + " " + f(bottom) + " " + f(bottomRight))
    }

    def checkShoulders = {
      this(grid +
        (shoulders(0) -> (grid(left) && grid(top))) +
        (shoulders(1) -> (grid(top) && grid(right))) +
        (shoulders(3) -> (grid(right) && grid(bottom))) +
        (shoulders(2) -> (grid(bottom) && grid(left)))
      )
    }

    def applyDig = {
      println("center:"+centerPos)
      print("before ")
      printGrid
      if (grid(centerPos)) {
        this(grid + (centerPos -> false)).checkShoulders
      } else {
        val c = shoulders.find(grid(_)).get
        this(grid + (c -> false))
      }
    }

    def topLeft = centerPos + Coord(-gridSize, -gridSize)
    def top:Coord = centerPos + Coord(0, -gridSize)
    def topRight = centerPos + Coord(gridSize, -gridSize)
    def left = centerPos + Coord(-gridSize, 0)
    def center = centerPos
    def right = centerPos + Coord(gridSize, 0)
    def bottomLeft = centerPos + Coord(-gridSize, gridSize)
    def bottom = centerPos + Coord(0, gridSize)
    def bottomRight = centerPos + Coord(gridSize, gridSize)


    def printPoints(l: List[Option[Coord]]) {
      def p(o:Option[Coord]) = o match {
        case Some(_)=>"x" case None => "o"
      }

      println("points:")
      println(p(l(0))+" "+p(l(1))+" " + p(l(2)) + "\n"+
              p(l(7))  + "   "   + p(l(3)) +"\n"+
              p(l(6))+" "+p(l(5))+" "+p(l(4)) )
    }

    def points = {
      print("after")
      printGrid
      //create list of points describing surface
      //TODO: determine if there are any ordering problems here
      val pointsList = List[Option[Coord]](
        checkPoint(topLeft, List[Coord](top, left, shoulders(0))),
        checkPoint(top, List(topLeft, shoulders(0), center, shoulders(1), topRight)),
        checkPoint(topRight, List[Coord](top, right, shoulders(1))),
        checkPoint(right, List[Coord](bottomRight, shoulders(3), center, shoulders(1), topRight)),
        checkPoint(bottomRight, List[Coord](bottom, shoulders(3), right)),
        checkPoint(bottom, List[Coord](bottomLeft, shoulders(2), center, shoulders(3), bottomRight)),
        checkPoint(bottomLeft, List[Coord](left, shoulders(2), bottom)),
        checkPoint(left, List[Coord](topLeft, shoulders(0), center, shoulders(2), bottomLeft))
      )
      printPoints(pointsList)
      pointsList.flatten
    }

    def checkPoint(c: Coord, neighbors: List[Coord]) = {
      if(neighbors.exists(!grid(_))) {
        Some(Coord(Math.round(c.x), Math.round(c.y)))
      } else {
        None
      }
    }
  }

  def segments = {
    Segment(points.last, points.head) :: (for(p <- points.sliding(2)) yield Segment(p(0), p(1))).toList
  }

  def shape = {
    val path = new Path2D.Double
    path.moveTo(points.head.x, points.head.y)
    points.tail.foreach((p:Coord) => {path.lineTo(p.x, p.y)})
    path.lineTo(points.head.x, points.head.y)
    path.closePath()
    path
  }

  def addAfter[A](l:List[A], value:A, after:A) = {
    l flatMap {x => if (x==value) List(value,after) else List(x)}
  }

  def dig(digPoint:Coord) = {
    def buildChunk(c:Coord) = {
      val allOffsets = full_offsets ::: half_offsets
      val map = allOffsets.map((t:(Int, Int))=> {
          val coord = c + Coord(t._1, t._2)
          val occupied = inOutline(coord) || !shape.contains(coord.x, coord.y)
          coord -> occupied
      }).toMap


      //TODO: work out clockwise vs non clockwise
      val clockwise = true
      Chunk(map, c, clockwise)
    }

    def tryChunk(c:Coord) = {
      val testPoints = c :: half_offsets.map((t:(Int, Int)) => Coord(t._1, t._2) + c)
      testPoints.exists(inOutline)
    }

    val testPoints = List(digPoint, digPoint + Coord(0, -gridSize), digPoint + Coord(0, gridSize))
    println("testPoints="+testPoints)
    val centerPoint = testPoints.find(tryChunk)
    println("foundPoint = "+centerPoint)
    centerPoint match {
      case None => (None,  this)
      case Some(c) => {
        (Some("stone"), this.merge(buildChunk(c).applyDig.points))
      }
    }
  }

  def merge(p:List[Coord]) = {
    //TODO: implement
    println("points:\n"+points)
    println("digs:\n"+p)
    val area = new Area(shape)
    val digArea = new Area(Shape(p).shape)
    area.add(digArea)
    val it = area.getPathIterator(null)
    val head = Array(0.0,0.0)
    var segType = it.currentSegment(head)
//    println("segType = "+segType)
    it.next()
    var path = List(Coord(head(0), head(1)))
    while(!it.isDone) {
      val c = Array(0.0, 0.0)

      segType = it.currentSegment(c)
      if(segType == PathIterator.SEG_LINETO) {
        path = Coord(c(0), c(1)) :: path
      } else {
        println(" code:"+segType + " at " + c(0)+","+c(1))
      }
      it.next()
    }

    Shape(path)
  }

  def inOutline(c:Coord) = {
    val exists = points.contains(c) || doesIntersect(c)
//    println("testing:"+c+" exists:"+exists)
    exists
  }


  def doesIntersect(c:Coord) = {
    segments.exists(isPointOnLine(c, _))
  }


  def intersect(c:Coord) = {
    segments.find(isPointOnLine(c, _))
  }

  val EPSILON = 0.01f;

  def isPointOnLine(point:Coord, segment:Segment) = {
    def compare(a:Double, b:Double) = {
      Math.max(a,b) - Math.min(a,b) < EPSILON
    }
//    println("isPointOnLine:"+point+" "+segment)
    val a = segment.a
    val b = segment.b

    val xInRange = point.x >= Math.min(a.x, b.x) && point.x <= Math.max(a.x, b.x)
    val yInRange = point.y >= Math.min(a.y, b.y) && point.y <= Math.max(a.y, b.y)
    if (!xInRange || !yInRange) {
      false
    } else if (compare(a.x,b.x)) {
      compare(a.x, point.x)
    } else if (compare(a.y, b.y)) {
      compare(a.y,point.y)
    } else {
      val aa = (b.y - a.y) / (b.x - a.x)
      val bb = a.y - aa * a.x
      val dist = math.abs(point.y - (aa*point.x+bb))
      if(dist < EPSILON) {
        println(""+point +" "+segment + " dist:"+dist + " epsilon:"+EPSILON)
      }
      dist < EPSILON
    }
  }
}

