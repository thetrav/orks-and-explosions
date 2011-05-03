package oae

import java.awt.Graphics2D

case class Scene(props:List[Prop]) {
  def draw(g:Graphics2D) {
    props.foreach(_.draw(g))
  }

  def +(prop:Prop) = Scene(prop :: props )
  def -(prop:Prop) = Scene(props - prop)
}