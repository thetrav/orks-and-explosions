package oae

import scala.collection.immutable.List
import java.awt._
import java.awt.geom._

case class Scene(props:List[Prop]) {
  val bg = Images.backgroundTile
  val backgroundPaint = new TexturePaint(bg, new Rectangle(0,0, bg.getWidth, bg.getHeight))

  val backgroundRectangle = new Rectangle(0,0,Main.width*3,Main.height*3)

  def draw(g:Graphics2D) {
    val playerPos = Physics.pos(Player.id)
    val screenSize = Coord(Main.width, Main.height)
    val offset = (playerPos % (screenSize)) + (screenSize)
    Debug.out("playerPos:"+playerPos)
    Debug.out("screenSize:"+screenSize)
    Debug.out("offset:"+offset)
    Main.camera.unTransform(g)
    g.translate(-offset.x, -offset.y)
    g.setPaint(backgroundPaint)
    g.fill(backgroundRectangle)
    g.translate(offset.x, offset.y)
    Main.camera.transform(g)

    props.foreach(_.draw(g))
  }

  def +(prop:Prop) = Scene(prop :: props )
  def -(prop:Prop) = Scene(props - prop)
}