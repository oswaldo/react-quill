package models

import java.util.UUID

import com.mohiva.play.silhouette.api.Identity
import com.mohiva.play.silhouette.api.LoginInfo

/**
 * The user object.
 *
 * @param email The email of the authenticated provider.
 * @param loginInfo The linked login info.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName Maybe the last name of the authenticated user.
 * @param fullName Maybe the full name of the authenticated user.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 */
case class User(
  email: String,
  loginInfo: LoginInfo,
  firstName: Option[String] = None,
  lastName: Option[String] = None,
  fullName: Option[String] = None,
  avatarURL: Option[String] = None)
    extends Identity
