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

import diode.Action
import models.SPAModel

object Login {

  case class State(visible: Boolean = true,
                   signIn: SignInData = SignInData("", "", false))

  class Backend(scope: BackendScope[SPAProps, State]) {

    def mounted(props: SPAProps) = Callback {}

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
      scope.modState(state =>
            state.copy(signIn = state.signIn.copy(rememberMe = v)))
    }

    def doLogin(props: SPAProps, state: State) = Callback {
      "/signIn".withData(write(state.signIn)) postAndRun {
        responseText: String =>
          val token = read[(String, String)](responseText)._2
          AjaxUtil.setToken(token)
          scope.modState(_.copy(visible = false)) >> props.tokenProxy
            .theDispatch(SPAModel.SetToken(token))
      }
    }

    def handleLogin(props: SPAProps,
                    state: State): ReactKeyboardEventI => Callback = e => {
      doLogin(props, state)
    }

    def handleLoginClick(props: SPAProps,
                         state: State): ReactEventH => Callback = e => {
      doLogin(props, state)
    }

    def render(props: SPAProps, state: State) = {
      val dispatch: Action => Callback = props.tokenProxy.theDispatch
      val actions = js.Array(
          MuiFlatButton(key = "1",
                        label = "Login",
                        secondary = true,
                        onTouchTap = handleLoginClick(props, state))())
      val component = <.div(
          css.Home.content,
          MuiDialog(title = "Login", actions = actions, open = state.visible)(
              <.form(
                  ^.onSubmit ==> handleLogin(props, state),
                  MuiTextField(floatingLabelText = "email",
                               value = state.signIn.email,
                               onChange = onEmailChange,
                               onEnterKeyDown = handleLogin(props, state))(),
                  <.br,
                  MuiTextField(floatingLabelText = "password",
                               `type` = "password",
                               value = state.signIn.password,
                               onChange = onPasswordChange,
                               onEnterKeyDown = handleLogin(props, state))(),
                  <.br,
                  MuiCheckbox(onCheck = onRememberMe)(),
                  "Remember me")
          ),
          "react-quill template")
      component
    }

  }

  val component = ReactComponentB[SPAProps]("HomePage")
    .initialState(State(visible = !AjaxUtil.hasToken))
    .renderBackend[Backend]
    .componentDidMount(scope => scope.backend.mounted(scope.props))
    .build

  def apply(props: SPAProps): ReactElement = {
    component(props)
  }

}
