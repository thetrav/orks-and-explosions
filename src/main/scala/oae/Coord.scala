package oae

case class Coord(x:Double, y:Double) {
  def + (o:Coord) = Coord(x + o.x, y + o.y)
  def - (o:Coord) = Coord(x - o.x, y - o.y)

  def * (s:Double) = Coord(x*s, y*s)
  def / (s:Double) = this * (1/s)

  def % (c:Coord) = Coord(x%c.x, y%c.y)

  def size() = math.sqrt(x*x + y*y)

  def dot(c:Coord) = x * c.x + y * c.y
  def cross(c:Coord) = (x*c.y) - (y*c.x)

  def rotate(angle:Double) = {
    val cos = math.cos(angle)
    val sin = math.sin(angle)
    //apply matrix [cos, sin] \n [-sin, cos]
//    [a,b] [c, d]  = [a*c+b*e, b*d+b*f]
//          [e, f]
    val newX = (x*cos) + (-1*y*sin)
    val newY = (x*sin) + (y*cos)
    Coord(newX, newY)
  }

  def distance(c:Coord) = math.sqrt((x-c.x)*(x-c.x)+(y-c.y)*(y-c.y))

  def normalize = this / math.sqrt(x*x + y*y)

  def offsetFrom(c:Coord) = this - c

  def round = Coord(math.round(x), math.round(y))
}
