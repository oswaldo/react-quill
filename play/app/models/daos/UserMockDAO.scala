package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.daos.UserMockDAO._

import scala.collection.mutable
import scala.concurrent.Future

/**
  * Give access to the user object.
  */
class UserMockDAO extends UserDAO {

  /**
    * Finds a user by its login info.
    *
    * @param loginInfo The login info of the user to find.
    * @return The found user or None if no user for the given login info could be found.
    */
  def find(loginInfo: LoginInfo) = {
    Future.successful(users.find {
      case (id, user) => user.loginInfo == loginInfo
    }.map(_._2))
  }

  /**
    * Finds a user by its user email.
    *
    * @param email The email of the user to find.
    * @return The found user or None if no user for the given email could be found.
    */
  def find(email: String) = {
    Future.successful(users.get(email))
  }

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User) = {
    users += (user.email -> user)
    Future.successful(user)
  }
}

/**
  * The companion object.
  */
object UserMockDAO {

  /**
    * The list of users.
    */
  val users: mutable.HashMap[String, User] = mutable.HashMap()
}
