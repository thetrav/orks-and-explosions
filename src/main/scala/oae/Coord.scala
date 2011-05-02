package oae



case class Coord(x:Double, y:Double) {
  def + (o:Coord) = Coord(x + o.x, y + o.y)
  def - (o:Coord) = Coord(x - o.x, y - o.y)

  def * (s:Double) = Coord(x*s, y*s)
}
