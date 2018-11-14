package models

import javax.inject.Inject
import play.api.db.DBApi

case class Edge(id: Int, head: String, tail: String, weight: Int)

@javax.inject.Singleton
class GraphRepository @Inject()(dbApi: DBApi){

  private val db = dbApi.database("default")


}
