package views

import scalatags.Text._

object MainView {
  import scalatags.Text.all._

  def apply(content: Seq[Modifier] = Seq.empty)(
      implicit env: play.Environment) = {
    html(
        body(content,
             // include the Scala.js scripts that sbt-play-scalajs has copied from the "client" 
             // project to the Play public target folder
             scripts("scalajsclient")))
  }

  def scripts(projectName: String)(implicit env: play.Environment) =
    Seq(projectScript(projectName, { if (env.isProd) "opt" else "fastopt" }),
        projectScript(projectName, "jsdeps"),
        projectScript(projectName, "launcher"),
        someScript("index-bundle"))

  def projectScript(projectName: String,
                    discriminator: String): TypedTag[String] = {
    val scriptName = s"${projectName.toLowerCase}-$discriminator"
    someScript(scriptName)
  }

  def someScript(scriptName: String) =
    script(src := s"/assets/$scriptName.js", `type` := "text/javascript")

}
