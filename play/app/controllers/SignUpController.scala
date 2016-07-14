package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import models.services.UserService
import shared.models.SignUpData
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits._
import upickle.default._
import play.api.mvc.{ Action, Controller }
import utils.auth.DefaultEnv

import scala.concurrent.Future

import org.slf4j.LoggerFactory
import ch.qos.logback.core.util.StatusPrinter
import ch.qos.logback.classic.LoggerContext
import SignUpController._

/**
 * The `Sign Up` controller.
 *
 * @param messagesApi The Play messages API.
 * @param silhouette The Silhouette stack.
 * @param userService The user service implementation.
 * @param authInfoRepository The auth info repository implementation.
 * @param avatarService The avatar service implementation.
 * @param passwordHasher The password hasher implementation.
 */
class SignUpController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher)
    extends Controller with I18nSupport {

  /**
   * Handles the submitted JSON data.
   *
   * @return The result to display.
   */
  def submit = Action.async(parse.json) { implicit request =>
    //    request.body.validate[SignUpData].map { data =>
    logger.debug(request.body.toString)
    val data = read[SignUpData](request.body.toString)
    val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
    userService.retrieve(loginInfo).flatMap {
      case Some(user) =>
        Future.successful(BadRequest(write("message" -> Messages("user.exists"))))
      case None =>
        val authInfo = passwordHasher.hash(data.password)
        val user = User(
          userID = UUID.randomUUID(),
          loginInfo = loginInfo,
          firstName = Some(data.firstName),
          lastName = Some(data.lastName),
          fullName = Some(data.firstName + " " + data.lastName),
          email = Some(data.email),
          avatarURL = None)
        for {
          avatar <- avatarService.retrieveURL(data.email)
          user <- userService.save(user.copy(avatarURL = avatar))
          authInfo <- authInfoRepository.add(loginInfo, authInfo)
          authenticator <- silhouette.env.authenticatorService.create(loginInfo)
          token <- silhouette.env.authenticatorService.init(authenticator)
        } yield {
          silhouette.env.eventBus.publish(SignUpEvent(user, request))
          silhouette.env.eventBus.publish(LoginEvent(user, request))
          Ok(write("token" -> token))
        }
    }
    //    }.recoverTotal {
    //      case error =>
    //        Future.successful(Unauthorized(Json.obj("message" -> Messages("invalid.data"))))
    //    }
  }
}

object SignUpController {
    def logger = LoggerFactory.getLogger(classOf[SignUpController])
}
