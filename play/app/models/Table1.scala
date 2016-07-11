package models

import java.util.UUID
import io.getquill._
import io.getquill.naming.SnakeCase

case class Table1(id: UUID, value: String) {

  implicit val encodeUUID = mappedEncoding[UUID, String](_.toString)
  implicit val decodeUUID = mappedEncoding[String, UUID](UUID.fromString(_))

}

object Table1 {

  lazy val db = source(new CassandraSyncSourceConfig[SnakeCase]("cassandra"))

  def list: List[Table1] = {
    val q = quote {
      query[Table1]
    }
    db.run(q)
  }

}
