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
import utils._
import utils.AjaxUtil._
import chandu0101.macros.tojs.GhPagesMacros
import chandu0101.scalajs.react.components.materialui._
import chandu0101.scalajs.react.components.Implicits._
import scala.scalajs.js

object HomePage {

  case class State(rows: Option[Seq[Table1]] = None, loginVisible: Boolean = true)

  class Backend(scope: BackendScope[Unit, State]) {

    val toggleLogin = scope.modState(state => state.copy(loginVisible = !state.loginVisible))
    
    def loginModal(state: State) = {
      val actions: ReactNode = js.Array(
        MuiFlatButton(key = "1", label = "Login", secondary = true, onTouchTap = handleLogin)()
      )
      <.div(
        MuiDialog(
          title = "Dialog With Actions",
          actions = actions,
          open = state.loginVisible,
          onRequestClose = CallbackDebug.f1("onRequestClose")
        )(
          "Dialog example with floating buttons"
        )
      )
    }

    def doLogin = "/signIn" getAnd { responseText: String =>
      println(s"responseText: $responseText")
    }

    def handleLogin: ReactEventH => Callback =
      e => {
        doLogin
        Callback.info("Login Clicked")
      }

    def loadRows = Callback {
      "/listTable1" getAndRun { responseText: String =>
        val result = read[Seq[Table1]](responseText)
        //no need to call runNow because it is called by getAndRun
        scope.modState(_.copy(rows = Some(result)))
      }
    }

    def render(state: State) = {
      println(s"Rendering $state")
      state.rows match {
        case None =>
          <.div(^.id := "home-content",
            css.Home.content,
            "react-quill template")

        case Some(rows) =>
          //ugly .toString just to prove the concept...
          <.div(^.id := "home-content", css.Home.content, s"${rows.toString}")
      }
    }

    def clear = Callback {
      //      println("Clear called")
    }

  }

  val component = ReactComponentB[Unit]("HomePage")
    .initialState(State())
    .renderBackend[Backend]
    .componentDidMount(_.backend.loadRows)
    .componentWillUnmount(_.backend.clear)
    .build

  def apply(): ReactElement = {
    component()
  }
}
