package shared.models

case class SignInData(
  email: String,
  password: String,
  rememberMe: Boolean)