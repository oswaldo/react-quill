package models.daos

import java.util.UUID
import io.getquill._
import io.getquill.naming.SnakeCase
import shared.models.Table1

object Table1DAO {

  implicit val encodeUUID = mappedEncoding[UUID, String](_.toString)
  implicit val decodeUUID = mappedEncoding[String, UUID](UUID.fromString(_))

  lazy val db = source(new CassandraSyncSourceConfig[SnakeCase]("cassandra"))

  def list: List[Table1] = {
    val q = quote {
      query[Table1]
    }
    db.run(q)
  }

}
