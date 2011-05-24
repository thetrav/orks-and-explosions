package oae

import javax.imageio.ImageIO
import java.awt._
import java.awt.geom._
import java.awt.image._
import scala.collection.immutable.List


object Images {
  def img(img:String) = ImageIO.read(getClass().getResourceAsStream(img+".png"))

  def tile(image:String) = img(("tiles/"+image))
  def decal(image:String) = img(("decals/"+image))

  val dwarf = img("dwarf")

  val backgroundTile = tile("bg_tile")
  val fgTiles = List(
    tile("fg_tile_1"),
    tile("fg_tile_2"),
    tile("fg_tile_3"),
    tile("fg_tile_4")
  )

  val decals = List(
    decal("decal_1"),
    decal("decal_2"),
    decal("decal_3"),
    decal("decal_4"),
    decal("decal_5"),
    decal("decal_6"),
    decal("decal_7")
  )

  def flipImageX(image:BufferedImage) = {
    val tx = AffineTransform.getScaleInstance(-1, 1);
    tx.translate(-image.getWidth(null), 0);
    val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    op.filter(image, null);
  }

  def flipAnimationX(src:Animation) = {
    Animation(for(frame <- src.frames) yield {
      Frame(flipImageX(frame.image), frame.time)
    }, src.currentFrame, src.timeCounter)
  }

  val dragonWalkSpeed = 10
  val dragonWalkRight = Animation(List(
    Frame(img("dragon/walk1"), dragonWalkSpeed*3),
    Frame(img("dragon/walk2"), dragonWalkSpeed),
    Frame(img("dragon/walk3"), dragonWalkSpeed*3),
    Frame(img("dragon/walk4"), dragonWalkSpeed)
  ), 0, 0)
  val dragonWalkLeft = flipAnimationX(dragonWalkRight)

  val dragonStand = Animation(List(
    Frame(img("dragon/walk1"), 1)
  ), 0, 0)

}