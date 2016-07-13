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

  case class State(rows: Option[Seq[Table1]] = None)

  class Backend(scope: BackendScope[Unit, State]) {

    def loadRows = Callback {
      import scala.concurrent.ExecutionContext.Implicits.global
      Ajax.get("/listTable1").onSuccess {
        case xhr =>
          val result = read[Seq[Table1]](xhr.responseText)
          scope.setState(State(Some(result)))
          dom.window.alert("Hi from Scala-js-dom")
      }
    }

    def render(state: State) = state.rows match {
      case None => <.div(
        ^.id := "home-content",
        css.Home.content, "react-quill template")

      case Some(rows) => <.div(
        ^.id := "home-content",
        css.Home.content, s"${rows.toString}")
    }

  }

  val component = ReactComponentB[Unit]("HomePage")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.loadRows)
    .build

  def apply(): ReactElement = {
    component()
  }
}
