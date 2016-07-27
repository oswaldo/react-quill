package routes

import japgolly.scalajs.react.extra.router.{Resolution, RouterConfigDsl, RouterCtl, _}
import japgolly.scalajs.react.vdom.prefix_<^._

import components.{TopNav, Footer}
import models.Menu
import pages.HomePage
import japgolly.scalajs.react.ReactComponentU

import circuit.SPACircuit
import components.SPAProps

object AppRouter {

  sealed trait AppPage

  case object Home extends AppPage
  case class Items(p: Item) extends AppPage

  val tokenConnection = SPACircuit.connect(_.token)

  val config = RouterConfigDsl[AppPage].buildConfig { dsl =>
    import dsl._
    val itemRoutes: Rule =
      Item.routes.prefixPath_/("#items").pmap[AppPage](Items) {
        case Items(p) => p
      }
    (trimSlashes
          | staticRoute(root, Home) ~> renderR(router =>
                tokenConnection(p =>
                      HomePage(SPAProps(p.modelReader, SPACircuit))))
          | itemRoutes)
      .notFound(redirectToPage(Home)(Redirect.Replace))
      .renderWith(layout _)
  }

  val mainMenu = Vector(
      Menu("Home", Home),
      Menu("Items", Items(Item.Info))
  )

  def layout(c: RouterCtl[AppPage], r: Resolution[AppPage]) = {
    <.div(
        TopNav(TopNav.Props(mainMenu, r.page, c)),
        r.render(),
        Footer()
    )
  }

  val baseUrl = BaseUrl.fromWindowOrigin / "react-scalajs-scalatags/"

  val router: ReactComponentU[Unit, _, Any, _] =
    Router(baseUrl, config.logToConsole)()
}
