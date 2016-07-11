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
import models.Table1
import java.util.UUID

class Application @Inject() (implicit env: play.Environment)
    extends Controller {

  implicit val table1Writes: Writes[Table1] = (
    (JsPath \ "id").write[UUID] and
    (JsPath \ "value").write[String])(unlift(Table1.unapply))

  implicit val table1Reads: Reads[Table1] = (
    (JsPath \ "id").read[UUID] and
    (JsPath \ "value").read[String])(Table1.apply _)

  def listTable1 = Action {
    val json = Json.toJson(Table1.list)
    Ok(json)
  }

  def index = ok()

  def ok(view: Seq[Text.TypedTag[String]] = Seq.empty) = Action {
    implicit val codec = Codec.utf_8
    Ok(MainView(view).toString).withHeaders(CONTENT_TYPE -> ContentTypes.HTML)
  }

}
