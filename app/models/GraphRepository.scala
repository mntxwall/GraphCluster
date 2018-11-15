package models

import anorm.{SQL, SqlParser}
import javax.inject.Inject
import play.api.Logger
import play.api.db.DBApi

case class Edge(id: Int, head: String, tail: String, weight: Int)

@javax.inject.Singleton
class GraphRepository @Inject()(dbApi: DBApi){

  private val db = dbApi.database("default")


  def checkTableExist(tableName: String): Int = {

    Logger.debug("Table name is " + tableName)
    val tableResult: Option[Int] = db.withConnection{ implicit c =>
      SQL("select count(tablename) from pg_tables where tablename = {tablename}")
        .on("tablename" -> s"$tableName")
        .as(SqlParser.scalar[Int].singleOpt)
    }
    //Logger.debug("" + tableResult.getOrElse(100))
    tableResult.getOrElse(0)

  }

  def createTable(tableName: String) = {

    val a = db.withConnection{ implicit c =>

      SQL(
        s"""
           create TABLE if not EXISTS $tableName (
            id serial PRIMARY KEY,
            vertexA VARCHAR (32),
            vertexB VARCHAR (32)
           )
         """ ).executeUpdate()

    }

    Logger.debug("Create table result is " +a.toString)
  }

}
