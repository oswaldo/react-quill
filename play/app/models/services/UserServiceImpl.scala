package models.services

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile

import javax.inject.Inject
import models.User
import models.daos.UserDAO
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
  * Handles actions to users.
  *
  * @param userDAO The user DAO implementation.
  */
class UserServiceImpl @Inject()(userDAO: UserDAO) extends UserService {

  /**
    * Retrieves a user that matches the specified login info.
    *
    * @param loginInfo The login info to retrieve a user.
    * @return The retrieved user or None if no user could be retrieved for the given login info.
    */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] =
    userDAO.find(loginInfo)

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User) = userDAO.save(user)

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile) = {
    userDAO.find(profile.loginInfo).flatMap {
      case Some(user) => // Update user with profile
        userDAO.save(
            user.copy(email = profile.email.get,
                      firstName = profile.firstName,
                      lastName = profile.lastName,
                      fullName = profile.fullName,
                      avatarURL = profile.avatarURL))
      case None => // Insert a new user
        userDAO.save(
            User(email = profile.email.get,
                 loginInfo = profile.loginInfo,
                 firstName = profile.firstName,
                 lastName = profile.lastName,
                 fullName = profile.fullName,
                 avatarURL = profile.avatarURL))
    }
  }
}
