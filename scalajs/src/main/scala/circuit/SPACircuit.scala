package circuit

import diode._
import diode.react._
import models.SPAModel

object SPACircuit extends Circuit[SPAModel] with ReactConnector[SPAModel] {

  def initialModel = SPAModel()

  val tokenHandler = new ActionHandler(
      zoomRW(_.token)((m, v) => m.copy(token = v))) {
    override def handle = {
      case SPAModel.SetToken(t) => updated(Some(t))
      case SPAModel.ClearToken => updated(None)
    }
  }

  val actionHandler = composeHandlers(tokenHandler)

}
