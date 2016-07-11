package pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scalacss.Defaults._
import scalacss.ScalaCssReact._
import org.scalajs.dom.ext.Ajax
import scala.concurrent.ExecutionContext

object HomePage {

  val contentDiv = <.div(
    ^.id := "home-content",
    css.Home.content,
    "react-quill template")

  val component = ReactComponentB
    .static("HomePage", contentDiv)
    .build

  def apply() = {
    component()
    import scala.concurrent.ExecutionContext.Implicits.global
    Ajax.get("/listTable1").onSuccess {
      case xhr =>
        
    }
  }
}
