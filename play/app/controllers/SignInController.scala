package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{ Clock, Credentials }
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import models.services.UserService
import net.ceedubs.ficus.Ficus._
import play.api.Configuration
import play.api.i18n.{ I18nSupport, Messages, MessagesApi }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import upickle.default._
import play.api.mvc.{ Action, Controller }
import utils.auth.DefaultEnv

import scala.concurrent.Future
import scala.concurrent.duration._

import shared.models.SignInData

import org.slf4j.LoggerFactory
import ch.qos.logback.core.util.StatusPrinter
import ch.qos.logback.classic.LoggerContext
import SignInController._

/**
 * The `Sign In` controller.
 *
 * @param messagesApi The Play messages API.
 * @param silhouette The Silhouette stack.
 * @param userService The user service implementation.
 * @param authInfoRepository The auth info repository implementation.
 * @param credentialsProvider The credentials provider.
 * @param socialProviderRegistry The social provider registry.
 * @param configuration The Play configuration.
 * @param clock The clock instance.
 */
class SignInController @Inject() (
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  credentialsProvider: CredentialsProvider,
  socialProviderRegistry: SocialProviderRegistry,
  configuration: Configuration,
  clock: Clock)
    extends Controller with I18nSupport {

  /**
   * Handles the submitted JSON data.
   *
   * @return The result to display.
   */
  def submit = Action.async(parse.json) { implicit request =>
    //    request.body.validate[SignInData].map { data =>
    logger.debug(request.body.toString)
    val data = read[SignInData](request.body.toString)
    credentialsProvider.authenticate(Credentials(data.email, data.password)).flatMap { loginInfo =>
      userService.retrieve(loginInfo).flatMap {
        case Some(user) => silhouette.env.authenticatorService.create(loginInfo).map {
          case authenticator if data.rememberMe =>
            val c = configuration.underlying
            authenticator.copy(
              expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
              idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout"))
          case authenticator => authenticator
        }.flatMap { authenticator =>
          silhouette.env.eventBus.publish(LoginEvent(user, request))
          silhouette.env.authenticatorService.init(authenticator).map { token =>
            Ok(write("token" -> token))
          }
        }
        case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
      }
    }.recover {
      case e: ProviderException =>
        Unauthorized(write("message" -> Messages("invalid.credentials")))
    }
    //    }.recoverTotal {
    //      case error =>
    //        Future.successful(Unauthorized(write("message" -> Messages("invalid.credentials"))))
    //    }
  }
}

object SignInController {
    def logger = LoggerFactory.getLogger(classOf[SignInController])
}
