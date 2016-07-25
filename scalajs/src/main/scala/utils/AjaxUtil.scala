package utils

import scala.concurrent.Future
import scala.scalajs.js

import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.XMLHttpRequest

import japgolly.scalajs.react.Callback

object AjaxUtil {
  
  private var token : Map[String, String] = Map.empty
  
  def setToken(t: String) = token = Map("Csrf-Token" -> t)
  
  def hasToken = !token.isEmpty
  
  def clearToken = token = Map.empty
  
  def get(url: String,
          data: Option[Ajax.InputData] = None,
          callback: Option[(String) => _] = None): Unit = {
    doCall(Ajax.get, url, data, callback)
  }

  def post(url: String,
           data: Option[Ajax.InputData] = None,
           callback: Option[(String) => _] = None): Unit = {
    doCall(Ajax.post, url, data, callback)
  }

  def doCall(method: (/*url:*/ String, /*data:*/ Ajax.InputData,
                      /*timeout:*/ Int, /*headers:*/ Map[String, String],
                      /*withCredentials:*/ Boolean,
                      /*responseType:*/ String) => Future[XMLHttpRequest],
             url: String,
             data: Option[Ajax.InputData] = None,
             callback: Option[(String) => _] = None,
             timeout: Int = 0,
             headers: Map[String, String] = token ++ Map(
                 "Content-Type" -> "application/json"),
             withCredentials: Boolean = false,
             responseType: String = ""): Unit = {

    import scala.concurrent.ExecutionContext.Implicits.global
    method
      .apply(url,
             data.getOrElse(null),
             timeout,
             headers,
             withCredentials,
             responseType)
      .onSuccess {
        case xhr =>
          callback.map(_.apply(xhr.responseText))
      }

  }

  case class AjaxCall(url: String,
                      data: Option[Ajax.InputData] = None,
                      callback: Option[(String) => _] = None) {

    def withData(data: Ajax.InputData) = copy(data = Some(data))

    def call(callback: (String) => _) = copy(callback = Some(callback))

    def callAndRun(callback: (String) => Callback) =
      copy(callback = Some { s: String =>
        callback.apply(s).runNow
      })

    def get = AjaxUtil.get(url, data, callback)

    def getAndRun(callback: (String) => Callback) = callAndRun(callback).get

    def post = AjaxUtil.post(url, data, callback)

    def postAndRun(callback: (String) => Callback) = callAndRun(callback).post

  }

  implicit def stringToAjaxCall(url: String) = AjaxCall(url)

}
