package oae.physics

import oae._

case class Segment(a:Coord, b:Coord) {

  def normal = Coord(0,-1)//TODO: calculate normal

  def intersect(other:Segment) = {
    val x1 = a.x
    val y1 = a.y
    val x2 = b.x
    val y2 = b.y
    val x3 = other.a.x
    val y3 = other.a.y
    val x4 = other.b.x
    val y4 = other.b.y

    val denominator = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1)
    if(denominator == 0) {
      None
    } else {
      val numeratorA = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)
      val numeratorB = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)

      val unknownA = numeratorA / denominator
      val unknownB = numeratorB / denominator
      if((unknownA < 1 && unknownA > 0) && (unknownB < 1 && unknownB > 0)) {
        val x = x1 + unknownA * (x2 - x1)
        val y = y1 + unknownA * (y2 - y1)
        Some(Coord(x,y))
      } else{
        None
      }
    }
  }

  def vector() = b - a

  def size() = {
    math.sqrt(((a.x - b.x) * (a.x - b.x)) + ((a.y - b.y) * (a.y - b.y)))
  }

  def rotate(angle:Double) = Segment(a.rotate(angle), b.rotate(angle))
  def + (c:Coord) = Segment(a + c, b + c)
  def - (c:Coord) = Segment(a - c, b - c)
}