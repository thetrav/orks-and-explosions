package oae

import java.awt.image.BufferedImage
import java.awt.Graphics2D

case class Frame(image:BufferedImage, time:Int)

case class Animation (frames:List[Frame], currentFrame:Int, timeCounter:Double) {
  def frame = frames(currentFrame)

  def update(animationSpeed:Double) = {
    val time = timeCounter + (1.0*animationSpeed)

    val newIndex = if(time > frame.time) {
      val next = currentFrame + 1
      if(next == frames.size) 0 else next
    } else {
      currentFrame
    }

    val newCounter = if(newIndex == currentFrame) time else 0

    Animation(frames, newIndex, newCounter)
  }

  def draw(g:Graphics2D, c:Coord) {
    g.drawImage(frame.image, null, c.x.asInstanceOf[Int], c.y.asInstanceOf[Int])
  }
}