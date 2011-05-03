package oae

import javax.imageio.ImageIO

object Images {
  def img(img:String) = ImageIO.read(getClass().getResourceAsStream(img+".png"))

  val dwarf = img("dwarf")
  val grass = img("grassblarg")
  val flowers = img("flowers")
  val statue = img("statue")
}