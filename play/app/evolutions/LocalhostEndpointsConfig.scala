package evolutions

import com.datastax.driver.core.Cluster

import de.leanovate.play.cassandra.evolutions.CassandraEndpointConfig

class LocalhostEndpointsConfig extends CassandraEndpointConfig {
  override def databases: Seq[String] = Seq("cassandra")

  override def clusterForDatabase(db: String): Cluster =
    Cluster.builder().addContactPoints("localhost").build()
}
