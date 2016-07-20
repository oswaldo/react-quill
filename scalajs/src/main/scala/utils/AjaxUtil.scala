package utils

import org.scalajs.dom.ext.Ajax
import japgolly.scalajs.react.Callback

object AjaxUtil {

  def get[R](url: String, callback: (String) => R): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    Ajax.get(url).onSuccess {
      case xhr =>
        callback.apply(xhr.responseText)
    }
  }

  implicit class AjaxString(url: String) {
    def getAnd(callback: (String) => _) {
      get(url, callback)
    }

    def getAndRun(callback: (String) => Callback) {
      get(url, {
        callback.apply(_).runNow
      })
    }
  }

}