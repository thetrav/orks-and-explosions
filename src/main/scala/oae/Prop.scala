package oae

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import oae.geom.Coord


case class Prop(position:Coord, image:BufferedImage) {
  def x = position.x.asInstanceOf[Int]
  def y = position.y.asInstanceOf[Int]

  def draw(g:Graphics2D) {
    g.drawImage(image, x, y, null)
  }
}