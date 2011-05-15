package oae

import java.awt.Graphics2D

object Scene {
  var props:List[Prop] = List()

  def draw(g:Graphics2D) {
    props.foreach(_.draw(g))
  }

  def initProps() {
    props = List(
      Prop(Coord(0,-10), Images.grass),
      Prop(Coord(200,20), Images.grass),
      Prop(Coord(3000,-20), Images.grass),
      Prop(Coord(-2000,10), Images.grass),
      Prop(Coord(34,12), Images.flowers),
      Prop(Coord(900,12), Images.statue)
    )
  }
}