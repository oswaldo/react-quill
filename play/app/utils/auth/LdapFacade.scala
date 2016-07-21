package utils.auth

import models.User

trait LdapFacade {

  def findByEmail(email: String): Option[User]

  def passByEmail(email: String): Option[String]

}
