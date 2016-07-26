package components

import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.Reusability
import japgolly.scalajs.react.extra.router.RouterCtl
import japgolly.scalajs.react.vdom.prefix_<^._

import scala.scalajs.js.{Any, UndefOr}
import scalacss.Defaults._
import scalacss.ScalaCssReact._
import routes.Item

import chandu0101.scalajs.react.components.materialui._
import chandu0101.scalajs.react.components.Implicits._
import scala.scalajs.js

import upickle.default._

import shared.models.SignInData
import utils._
import utils.AjaxUtil._

object Login {
  
  case class State(visible: Boolean = true,
                   signIn: SignInData = SignInData("", "", false))

  class Backend(scope: BackendScope[Unit, State]) {

    def onEmailChange: ReactEventI => Callback = e => {
      val newValue = e.target.value
      scope.modState(state =>
            state.copy(signIn = state.signIn.copy(email = newValue)))
    }

    def onPasswordChange: ReactEventI => Callback = e => {
      val newValue = e.target.value
      scope.modState(state =>
            state.copy(signIn = state.signIn.copy(password = newValue)))
    }

    def onRememberMe: (ReactEventH, Boolean) => Callback = (e, v) => {
      scope.modState(
          state =>
            state.copy(signIn =
                  state.signIn.copy(rememberMe = v)))
    }

    def doLogin = Callback {
      val state = scope.state.runNow
      "/signIn".withData(write(state.signIn)) postAndRun { responseText: String =>
        val token = read[(String,String)](responseText)._2
        AjaxUtil.setToken(token)
        scope.modState(_.copy(visible = false))
      }
    }

    def handleLogin: ReactKeyboardEventI => Callback = e => {
        doLogin
      }

    def handleLoginClick: ReactEventH => Callback = e => {
        doLogin
      }

    def render(state: State) = {
      val actions = js.Array(
          MuiFlatButton(key = "1",
                        label = "Login",
                        secondary = true,
                        onTouchTap = handleLoginClick)())
      val component = <.div(
          css.Home.content,
          MuiDialog(title = "Login",
                    actions = actions,
                    open = state.visible)(
              <.form(^.onSubmit ==> handleLogin,
                     MuiTextField(floatingLabelText = "email", 
                                  value = state.signIn.email,
                                  onChange = onEmailChange,
                                  onEnterKeyDown = handleLogin
                                  )(),
                     <.br,
                     MuiTextField(floatingLabelText = "password", 
                                  `type` = "password",
                                  value = state.signIn.password,
                                  onChange = onPasswordChange,
                                  onEnterKeyDown = handleLogin
                                  )(),
                     <.br,
                     MuiCheckbox(onCheck = onRememberMe)(),
                     "Remember me")
          ),
          "react-quill template")
      component
    }

  }
  
  val component = ReactComponentB[Unit]("HomePage")
    .initialState(State(visible = !AjaxUtil.hasToken))
    .renderBackend[Backend]
    .build

  def apply(): ReactElement = {
    component()
  }
  
}