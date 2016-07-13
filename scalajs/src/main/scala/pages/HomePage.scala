package pages

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.prefix_<^._

import scalacss.Defaults._
import scalacss.ScalaCssReact._
import org.scalajs.dom.ext.Ajax
import scala.concurrent.ExecutionContext
import shared.models.Table1
import upickle.default._
import org.scalajs.dom

object HomePage {
  
  case class State(rows: Option[Seq[Table1]])

  val contentDiv = <.div(
    ^.id := "home-content",
    css.Home.content,
    "react-quill template")

  val component = ReactComponentB
    .static("HomePage", contentDiv)
    .build

  def apply(): ReactElement = {
    import scala.concurrent.ExecutionContext.Implicits.global
    Ajax.get("/listTable1").onSuccess {
      case xhr =>
        var content = read[Seq[Table1]](xhr.responseText)
//        dom.window.alert("Hi from Scala-js-dom")
        contentDiv(<.p(content.toString))
    }
    component()
  }
}
