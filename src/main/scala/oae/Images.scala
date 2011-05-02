package oae

import javax.imageio.ImageIO

object Images {
  val dwarf = ImageIO.read(getClass().getResourceAsStream("dwarf.png"))
}