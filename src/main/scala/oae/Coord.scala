package oae

case class Coord(x:Double, y:Double) {
  def + (o:Coord) = Coord(x + o.x, y + o.y)
  def - (o:Coord) = Coord(x - o.x, y - o.y)

  def * (s:Double) = Coord(x*s, y*s)

  private def min(n:Double, min:Double) = if (n < min && n > min * (-1)) 0 else n

  def cutOffAt(minimum:Double) = Coord (
    min(x, minimum), min(y,minimum))
}
