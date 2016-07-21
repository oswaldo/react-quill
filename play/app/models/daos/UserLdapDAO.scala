package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.collection.mutable
import scala.concurrent.Future
import utils.auth.LdapFacade
import javax.inject.Inject

/**
  * Give access to the user object.
  */
class UserLdapDAO @Inject()(ldap: LdapFacade) extends UserDAO {

  /**
    * Finds a user by its login info.
    *
    * @param loginInfo The login info of the user to find.
    * @return The found user or None if no user for the given login info could be found.
    */
  def find(loginInfo: LoginInfo) = {
    find(loginInfo.providerKey)
  }

  /**
    * Finds a user by its user email.
    *
    * @param email The email of the user to find.
    * @return The found user or None if no user for the given email could be found.
    */
  def find(email: String) = {
    Future.successful(ldap.findByEmail(email))
  }

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User) = {
    ???
  }
}
