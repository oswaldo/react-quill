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
import models.Table1DAO
import java.util.UUID
import upickle.default._

class Application @Inject()(implicit env: play.Environment)
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

}
