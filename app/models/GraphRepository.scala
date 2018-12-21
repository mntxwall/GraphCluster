package models

import anorm.{Macro, ResultSetParser, RowParser, SQL, SqlParser, ~}
import javax.inject.Inject
import play.api.Logger
import play.api.db.DBApi
import anorm.SqlParser._
import play.api.libs.json.{JsPath, Json, Writes}

//case class Edge(id: Int, head: String, tail: String, weight: Int)
case class Edge(id: Int, vertexa: String, vertextb: String)

case class D3GraphNodes(id: String)

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
     db.withConnection{ implicit c =>
      SQL(
        s"""
           create TABLE if not EXISTS $tableName (
            id serial PRIMARY KEY,
            vertexA VARCHAR (32),
            vertexB VARCHAR (32)
           )
         """ ).executeUpdate()

    }
  }

  def insertFromFile(fileWithPath: String, tbName: String) = {
    db.withConnection{ implicit c =>
      SQL(s"COPY $tbName(vertexA,vertexB) FROM '$fileWithPath' with DELIMITER ','").execute()
    }

  }

  def getVertex(tbName: String): Set[String] = {
    db.withConnection{ implicit c =>
      SQL(s"Select vertexa from $tbName").as(SqlParser.scalar[String].*)
    }.toSet ++ db.withConnection{ implicit c =>
      SQL(s"Select vertexb from $tbName").as(SqlParser.scalar[String].*)
    }.toSet
  }

  def getEdgesString(tbName: String) = {
    val rowParser: RowParser[String] = {
      get[String]("vertexa") ~
        get[String]("vertexb") map {
        case vertexa~vertexb => vertexa + "_" + vertexb
      }
    }

    db.withConnection{ implicit c =>
      SQL(s"SELECT vertexa, vertexb from $tbName").as(rowParser.*)
    }

  }

  def getEdges(tbName: String) = {

    //val rowParser: RowParser[Edge] = Macro.parser[Edge]("vertexa", "vertexb")

    //val rowParser = str("vertexa") ~ str("vertexb") map { case n ~ p => (n, p) }

    //var rowParser = {str("vertexa") ~ str("vertexb") map(flatten) *}

    val rowParser: RowParser[Array[String]] = {
      get[String]("vertexa") ~
        get[String]("vertexb") map {
        case vertexa~vertexb => Array(vertexa, vertexb)
      }
    }

    db.withConnection{ implicit c =>
      SQL(s"SELECT vertexa, vertexb from $tbName").as(rowParser.*)
    }
  }

}
