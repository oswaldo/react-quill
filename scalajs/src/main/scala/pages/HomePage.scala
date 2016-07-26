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

import shared.models.SignInData

object HomePage {

  case class State(rows: Option[Seq[Table1]] = None,
                   loginVisible: Boolean = true,
                   signIn: SignInData = SignInData("", "", false))

  class Backend(scope: BackendScope[Unit, State]) {

    val toggleLogin =
      scope.modState(state => state.copy(loginVisible = !state.loginVisible))

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
//      Callback.log(s"remember $v") >>
      scope.modState(
          state =>
            state.copy(signIn =
                  state.signIn.copy(rememberMe = v)))
    }

    def renderLogin(state: State) = {
      val actions = js.Array(
          MuiFlatButton(key = "1",
                        label = "Login",
                        secondary = true,
                        onTouchTap = handleLoginClick)())
      val component = <.div(
          css.Home.content,
          MuiDialog(title = "Login",
                    actions = actions,
                    open = state.loginVisible)(
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

    def doLogin = Callback {
      val state = scope.state.runNow
      "/signIn".withData(write(state.signIn)) postAndRun { responseText: String =>
        val token = read[(String,String)](responseText)._2
        AjaxUtil.setToken(token)
        scope.modState(_.copy(loginVisible = false)) >> loadList
      }
    }

    def handleLogin: ReactKeyboardEventI => Callback = e => {
        doLogin >>
        Callback.log(scope.state.runNow.toString)
      }

    def handleLoginClick: ReactEventH => Callback =
      e => {
        doLogin >>
        Callback.log(scope.state.runNow.toString)
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

    def render(state: State): ReactElement = {
      if (state.loginVisible) {
        val component = renderLogin(state)
        component
      } else {
        val component = state.rows match {
          case None =>
            <.div(^.id := "home-content",
                  css.Home.content,
                  "react-quill template")

          case Some(rows) =>
            //ugly .toString just to prove the concept...
            <.div(^.id := "home-content",
                  css.Home.content,
                  s"${rows.toString}")
        }
        component
      }
    }

    def clear = Callback.log("Clear called")

  }
  
  val component = ReactComponentB[Unit]("HomePage")
    .initialState(State(loginVisible = !AjaxUtil.hasToken))
    .renderBackend[Backend]
    .componentDidMount(_.backend.init)
    .componentWillUnmount(_.backend.clear)
    .build

  def apply(): ReactElement = {
    component()
  }
}
