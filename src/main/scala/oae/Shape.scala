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

  def applyDig = {
    print("before ")
    printGrid
    if (grid(centerPos)) {
      this(grid + (centerPos -> false))
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


  def points = {
    print("after")
    printGrid
    //create list of points describing surface
    //TODO: determine if there are any ordering problems here
    List[Option[Coord]](
      checkPoint(topLeft, List[Coord](top, left, shoulders(0))),
      checkPoint(top, List(topLeft, shoulders(0), center, shoulders(1), topRight)),
      checkPoint(topRight, List[Coord](top, right, shoulders(1))),
      checkPoint(left, List[Coord](topLeft, shoulders(0), center, shoulders(2), bottomLeft)),
      checkPoint(right, List[Coord](bottomRight, shoulders(3), center, shoulders(1), topRight)),
      checkPoint(bottomLeft, List[Coord](left, shoulders(2), bottom)),
      checkPoint(bottom, List[Coord](bottomLeft, shoulders(2), center, shoulders(3), bottomRight)),
      checkPoint(bottomRight, List[Coord](bottom, shoulders(3), right))
    ).flatten
  }

  def checkPoint(c: Coord, neighbors: List[Coord]) = {
    if(grid(c) && neighbors.exists(!grid(_))) {
      Some(c)
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
    val centerPoint = testPoints.find(tryChunk)
    centerPoint match {
      case None => this
      case Some(c) => {
        this.merge(buildChunk(c).applyDig.points)
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
//      println("segtype = "+segType)
      path = Coord(c(0), c(1)) :: path
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

  val EPSILON = 0.1f;

  def isPointOnLine(point:Coord, segment:Segment) = {
//    println("isPointOnLine:"+point+" "+segment)
    val a = segment.a
    val b = segment.b
    if (a.x == b.x && a.x == point.x) {
      (point.y < a.y && point.y > b.y) || (point.y < b.y && point.y > a.y)
    } else if (a.y == b.y && a.y == point.y) {
      (point.x < a.x && point.x > b.x) || (point.x < b.x && point.x > a.x)
    } else {
      val aa = (b.y - a.y) / (b.x - a.x)
      val bb = a.y - aa * a.x
      math.abs(point.y - (aa*point.x+bb)) < EPSILON
    }
  }

  /**
   * isPointOnLine:Coord(600.0,240.0) Segment(Coord(600.0,30.0),Coord(600.0,300.0))
   * (300 - 30) / (600 - 600)
   */
}

