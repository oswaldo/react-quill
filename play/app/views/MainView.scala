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
    Seq(bundle("index"),
    		bundle("material_ui"),
        projectScript(projectName, { if (env.isProd) "opt" else "fastopt" }),
//        projectScript(projectName, "jsdeps"),
        projectScript(projectName, "launcher")
//        ,
//        bundle("react_geom_icons"),
//        bundle("react_infinite"),
//        bundle("react_select"),
//        bundle("react_slick"),
//        bundle("react_spinner"),
//        bundle("react_tags_input"),
//        bundle("elemental_ui"),
        )

  def projectScript(projectName: String,
                    discriminator: String): TypedTag[String] = {
    val scriptName = s"${projectName.toLowerCase}-$discriminator"
    someScript(scriptName)
  }
  
  def bundle(bundlePrefix: String) = someScript(s"$bundlePrefix-bundle")

  def someScript(scriptName: String) =
    script(src := s"/assets/$scriptName.js", `type` := "text/javascript")

}
