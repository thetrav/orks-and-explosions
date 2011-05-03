
import oae._
import oae.physics._

import org.specs.Specification

class TestMotion extends Specification {

  "Motion move" should {
    "correctly model friction" in {
      var entity = Motion(Coord(1,0), Coord(0,0), Coord(0,0))

      println("accell, , velocity, , position, ")
      println("x,y,x,y,x,y")
      List(0,1,2,3,4,5,6,7,8,9,10).foreach( _ => {
        println(entity.accelleration.x+","+entity.accelleration.y +","+ entity.velocity.x +","+ entity.velocity.y +","+ entity.position.x +","+ entity.position.y)
        entity = entity.move()
      })
      entity = entity.accell(Coord(0,0))
      List(0,1,2,3,4,5,6,7,8,9,10).foreach( _ => {
        println(entity.accelleration.x+","+entity.accelleration.y +","+ entity.velocity.x +","+ entity.velocity.y +","+ entity.position.x +","+ entity.position.y)
        entity = entity.move()
      })

      1
    }
  }
}