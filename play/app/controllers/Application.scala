package controllers

import play.api._
import play.api.http._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scalatags._
import scalatags.Text._
import scalatags.Text.all._
import shared.SharedMessages
import javax.inject.Inject
import views.MainView
import models.daos.Table1DAO
import java.util.UUID
import upickle.default._
import com.mohiva.play.silhouette.api.{ LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import utils.auth.DefaultEnv
import scala.concurrent.Future

class Application @Inject() (silhouette: Silhouette[DefaultEnv],
  socialProviderRegistry: SocialProviderRegistry,
  implicit val env: play.Environment)
    extends Controller {

  def listTable1 = Action {
    val json = write(Table1DAO.list)
    Ok(json)
  }

  def index = ok()

  def ok(view: Seq[Text.TypedTag[String]] = Seq.empty) = Action {
    implicit val codec = Codec.utf_8
    Ok(MainView(view).toString).withHeaders(CONTENT_TYPE -> ContentTypes.HTML)
  }

  /**
   * Returns the user.
   *
   * @return The result to display.
   */
  def user = silhouette.SecuredAction.async { implicit request =>
    Future.successful(Ok(write(request.identity)))
  }

  /**
   * Manages the sign out action.
   */
  def signOut = silhouette.SecuredAction.async { implicit request =>
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, Ok)
  }

}
