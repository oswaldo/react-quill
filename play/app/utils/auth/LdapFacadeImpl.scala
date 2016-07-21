package utils.auth

import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Logger
import com.mohiva.play.silhouette.api.LoginInfo
import com.unboundid.ldap.listener.InMemoryDirectoryServer
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig
import com.unboundid.ldap.listener.InMemoryListenerConfig
import com.unboundid.ldap.sdk.SearchResultEntry
import com.unboundid.ldif.LDIFReader

import javax.inject.Inject
import javax.inject.Singleton
import models.User
import play.api.inject.ApplicationLifecycle
import com.unboundid.ldap.sdk.SearchScope
import com.unboundid.ldap.sdk.Filter
import com.unboundid.ldap.sdk.DereferencePolicy

@Singleton
class LdapFacadeImpl @Inject()(lifecycle: ApplicationLifecycle)
    extends LdapFacade
    with Logger {

  val domainName = "example"

  val domainExtension = "com"

  val nameDc = s"dc=$domainName"

  val extensionDc = s"dc=$domainExtension"

  @volatile private var server = {
    val config = new InMemoryDirectoryServerConfig(s"$nameDc,$extensionDc")

    config.addAdditionalBindCredentials(
        s"cn=admin@$domainName.$domainExtension",
        "password")
    config.setListenerConfigs(
        InMemoryListenerConfig.createLDAPConfig("default", 2389))
    config.setSchema(null)

    val server = new InMemoryDirectoryServer(config)
    logger.debug(this.getClass.getResource("/ldap/sample.ldif").getPath)
    server.importFromLDIF(
        true,
        new LDIFReader(this.getClass.getResourceAsStream("/ldap/sample.ldif")))
    server.startListening

    server
  }

  lifecycle.addStopHook { () =>
    Future.successful(server.shutDown(true))
  }

  def authenticate(email: String, password: String): Option[User] = {

    val auth = server.bind(s"cn=$email", password)

    if (auth.getResultCode.isConnectionUsable) {
      findByEmail(email)
    } else {
      None
    }
  }

  def findByEmail(email: String): Option[User] = {
    val entry = entryByEmail(email)
    //    val permission = entry.map(_.getAttributeValue("sn").toString)
    entry.map(e => User(email, LoginInfo("ldap", email)))
  }

  def passByEmail(email: String): Option[String] =
    entryByEmail(email, "userpassword").map { entry =>
      entry.getAttributeValue("userpassword")
    }

  def entryByEmail(email: String,
                   attributes: String*): Option[SearchResultEntry] = {
    val conn = server.getConnection
    val entries = {
      val queryDn = s"ou=Users,$nameDc,$extensionDc"
      val attributeFilter = Filter.createEqualityFilter("mail", email)
      conn.search(queryDn,
                  SearchScope.ONE,
                  DereferencePolicy.NEVER,
                  1,
                  0,
                  false,
                  attributeFilter,
                  attributes: _*)
    }
    val result =
      if (entries == null || entries.getEntryCount == 0) None
      else {
        assert(entries.getEntryCount == 1)
        Some(entries.getSearchEntries.get(0))
      }
    logger.trace(
        result.map(e => s"Found $e").getOrElse(s"Email $email not found"))
    conn.close
    result
  }
}
