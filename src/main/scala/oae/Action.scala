package oae



trait Action

case class JumpAction(preparing:Boolean, counter:Int) extends Action {
  def tick() = JumpAction(preparing, counter + 1)
  def jumping(jumping:Boolean) = JumpAction(jumping, counter)
}