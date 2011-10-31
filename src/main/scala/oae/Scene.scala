package oae

import scala.collection.immutable.List
import java.awt._
import java.awt.geom._
import physics._

case class Scene(props:List[Prop]) {

  val default = Images.fgTiles.head
  val defaultPaint = new TexturePaint(default, new Rectangle(0,0, default.getWidth, default.getHeight))

  val backgroundRectangle = new Rectangle(0,0,Game.width*3,Game.height*3)

  val background= Images.backgroundTile
  val backgroundPaint = new TexturePaint(background, new Rectangle(0,0, background.getWidth, background.getHeight))

  def draw(g:Graphics2D) {
    val playerPos = Physics.pos(Player.id)
    val screenSize = Coord(Game.width, Game.height)
    val offset = (playerPos % (screenSize)) + (screenSize)
    Debug.out("playerPos:"+playerPos)
    Debug.out("screenSize:"+screenSize)
    Debug.out("offset:"+offset)
    Game.camera.unTransform(g)
    g.translate(-offset.x, -offset.y)
    g.setPaint(defaultPaint)
    g.fill(backgroundRectangle)
    g.translate(offset.x, offset.y)
    Game.camera.transform(g)

    g.setPaint(backgroundPaint)
    g.fill(Physics.world.shape)

    props.foreach(_.draw(g))
  }

  def +(prop:Prop) = Scene(prop :: props )
  def -(prop:Prop) = Scene(props - prop)
}