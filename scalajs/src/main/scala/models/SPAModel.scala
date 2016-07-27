package models

import diode.Action

case class SPAModel(token: Option[String] = None)

object SPAModel {

  case class SetToken(token: String) extends Action

  case object ClearToken extends Action

}
