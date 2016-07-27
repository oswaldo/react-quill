import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.Dynamic.global
import org.scalajs.dom
import scalatags.JsDom.all._
import shared.SharedMessages
import css.AppCSS
import routes.AppRouter
import circuit.SPACircuit
import models.SPAModel
import diode.dev.{Hooks, PersistStateIDB}
import upickle.default._
import japgolly.scalajs.react._
import japgolly.scalajs.react.extra.router._

@JSExport("Hello")
object Hello extends JSApp {

  def main(): Unit = {
    AppCSS.load
    Hooks.hookPersistState("test", SPACircuit)
    SPACircuit.dispatch(SPAModel.ClearToken)
    ReactDOM.render(AppRouter.router, dom.document.getElementById("viewport"))
  }

}
