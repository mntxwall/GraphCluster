package models

import anorm.{SQL, SqlParser}
import javax.inject.Inject
import play.api.Logger
import play.api.db.DBApi

case class Edge(id: Int, head: String, tail: String, weight: Int)

@javax.inject.Singleton
class GraphRepository @Inject()(dbApi: DBApi){

  private val db = dbApi.database("default")


  def checkTableExist(tableName: String) = {

    Logger.debug("Table name is " + tableName)
    val tableResult: Option[String] = db.withConnection{ implicit c =>
      SQL("select tablename from pg_tables where tablename = {tablename}").on("tablename" -> s"$tableName").as(SqlParser.scalar[String].singleOpt)
    }
    Logger.debug(tableResult.getOrElse("Bad"))
  }

}