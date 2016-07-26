package models

case class SPAModel(token: Option[String] = None)

object SPAModel {
  
  case class SetToken(token: String)

  case class ClearToken()

}
