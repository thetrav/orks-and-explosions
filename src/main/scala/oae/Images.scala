package oae

import javax.imageio.ImageIO
import java.awt._
import java.awt.geom._
import java.awt.image._
import scala.collection.immutable.List


object Images {
  def img(img:String) = ImageIO.read(getClass().getResourceAsStream(img+".png"))

  val dwarf = img("dwarf")
  val grass = img("grassblarg")
  val flowers = img("flowers")
  val statue = img("statue")

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

  val dwarfWalkSpeed = 10
  val dwarfWalkRight = Animation(List(
    Frame(img("dwarf/anim1"), dwarfWalkSpeed),
    Frame(img("dwarf/anim2"), dwarfWalkSpeed),
    Frame(img("dwarf/anim3"), dwarfWalkSpeed),
    Frame(img("dwarf/anim4"), dwarfWalkSpeed),
    Frame(img("dwarf/anim5"), dwarfWalkSpeed),
    Frame(img("dwarf/anim6"), dwarfWalkSpeed),
    Frame(img("dwarf/anim7"), dwarfWalkSpeed)
  ), 0, 0)
  val dwarfWalkLeft = flipAnimationX(dwarfWalkRight)
  val dwarfStand = Animation(List(
    Frame(img("dwarf/anim4"), dwarfWalkSpeed)
  ), 0, 0)

}