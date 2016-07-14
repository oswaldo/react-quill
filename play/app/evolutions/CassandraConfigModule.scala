package evolutions

import de.leanovate.play.cassandra.evolutions.CassandraEndpointConfig
import play.api.Configuration
import play.api.Environment
import play.api.inject.Module

class CassandraConfigModule extends Module {
  override def bindings(environment: Environment,
                        configuration: Configuration) = {
    Seq(bind[CassandraEndpointConfig].to[LocalhostEndpointsConfig])
  }
}
