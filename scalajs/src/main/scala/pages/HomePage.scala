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

import components.Login
import components.SPAProps
import shared.models.SignInData
import diode.react.ModelProxy

object HomePage {

  case class State(rows: Option[Seq[Table1]] = None,
                   loginState: Login.State = Login.State())

  class Backend(scope: BackendScope[SPAProps, State]) {

    def mounted(props: SPAProps) = Callback {}

    def onEmailChange: ReactEventI => Callback = e => {
      val newValue = e.target.value
      scope.modState(
          state =>
            state.copy(loginState = state.loginState.copy(
                    signIn = state.loginState.signIn.copy(email = newValue))))
    }

    def onPasswordChange: ReactEventI => Callback = e => {
      val newValue = e.target.value
      scope.modState(
          state =>
            state.copy(loginState = state.loginState.copy(signIn =
                      state.loginState.signIn.copy(password = newValue))))
    }

    def onRememberMe: (ReactEventH, Boolean) => Callback = (e, v) => {
//      Callback.log(s"remember $v") >>
      scope.modState(
          state =>
            state.copy(loginState = state.loginState.copy(
                    signIn = state.loginState.signIn.copy(rememberMe = v))))
    }

    def init = Callback {
      if (AjaxUtil.hasToken) loadList.runNow
    }

    def loadList = Callback {
      "/listTable1" getAndRun { responseText: String =>
        val result = read[Seq[Table1]](responseText)
        //no need to call runNow because it is called by getAndRun
        scope.modState(_.copy(rows = Some(result)))
      }
    }

    def render(props: SPAProps, state: State): ReactElement = {
      if (!AjaxUtil.hasToken) {
        Login(props)
      } else {
        val component = state.rows match {
          case None =>
            <.div(^.id := "home-content",
                  css.Home.content,
                  "react-quill template")

          case Some(rows) =>
            //ugly .toString just to prove the concept...
            <.div(
                ^.id := "home-content",
                css.Home.content,
                s"${rows.toString}",
                <.br,
                MuiAvatar(
                    size = 100,
                    src =
                      "https://pixabay.com/static/uploads/photo/2014/10/22/16/39/tools-498202_960_720.jpg")(),
                <.br,
                "It Works!")
        }
        component
      }
    }

    def clear = Callback.log("Clear called")

  }

  private val component = ReactComponentB[SPAProps]("HomePage")
    .initialState(
        State(loginState = Login.State(visible = !AjaxUtil.hasToken)))
    .renderBackend[Backend]
    .componentDidMount(scope =>
          scope.backend.mounted(scope.props) >> scope.backend.init)
    .componentWillUnmount(_.backend.clear)
    .build

  def apply(tokenProxy: ModelProxy[Option[String]]): ReactElement = {
    component(SPAProps(tokenProxy))
  }

}
