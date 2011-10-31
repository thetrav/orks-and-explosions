import sbt._
import SignJar._

class OrksAndExplosionsProject(info: ProjectInfo) extends DefaultWebstartProject(info) with IdeaProject {
  
//  override def webstartSignConfiguration = Some(new SignConfiguration("mark", storePassword("fakepassword") :: Nil))

  val specsVersion = crossScalaVersionString match {
    case "2.8.0" => "1.6.5"
    case _ => "1.6.6"
  }
  val specs = "org.scala-tools.testing" % ("specs_" + crossScalaVersionString) % specsVersion % "test"

  def jnlpXML(libraries: Seq[WebstartJarResource]) =
      <jnlp spec="1.0+" codebase="http://localhost/~thetrav/orks" href={artifactBaseName + ".jnlp"}>
        <information>
          <title>Webstart Test</title>
          <vendor>Vendor</vendor>
          <description>Webstart test</description>
          <offline-allowed />
        </information>
        <resources>
          <j2se version="1.5+" />
          { defaultElements(libraries) }
        </resources>
        <application-desc main-class="oae.Main" />
      </jnlp>
}


// vim: set ts=4 sw=4 et:
