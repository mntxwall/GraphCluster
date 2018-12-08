package controllers

import javax.inject.{Inject, Singleton}
import models.{CPMRepository, ClusterHash, D3GraphNodes, GraphRepository}
import org.jgrapht.graph.DefaultEdge
import play.api.libs.json.{JsObject, JsPath, Json, Writes}
import play.api.libs.functional.syntax._
import play.api.mvc.{AbstractController, ControllerComponents}

import collection.JavaConverters._
import views.html.D3.d3view

import scala.collection.immutable.HashMap
import scala.collection.mutable
//import controllers

@Singleton
class D3Controller @Inject()(cc: ControllerComponents,
                               graphRepository: GraphRepository) extends AbstractController(cc){


  //implicit val residentWrites = Json.writes[ClusterHash]

  implicit val ClusterWrites: Writes[ClusterHash] = (
    (JsPath \ "clique").write[Int] and
      (JsPath \ "cluster").write[Set[Set[String]]]
    )(unlift(ClusterHash.unapply))

  /*
  implicit val D3NodesWrites: Writes[D3GraphNodes] = (
    (JsPath \ "id").write[String] and
    )(unlift(D3GraphNodes.unapply))*/


  def datas() = Action{
    val cpm = new CPMRepository(graphRepository)
    //println(aa.CreateGraph())
    val graph = cpm.CreateGraph()

    val gv = graph.vertexSet().asScala.map{x =>

      Json.obj("id" -> x)
    }


    val gl = graph.edgeSet().asScala.map{x: DefaultEdge =>
      Json.obj("source" -> graph.getEdgeSource(x), "target" -> graph.getEdgeTarget(x))
      //cliqueHashMap.apply(2) += setVertex
    }

    val clusterResult:Set[ClusterHash] = cpm.findCPMCluster2(cpm.getCliques2()).toSet

    println(clusterResult)

    Ok(Json.obj("nodes" -> gv, "links" -> gl))

  }

  def index() = Action{


    val cpm = new CPMRepository(graphRepository)
    cpm.CreateGraph()
    val clusterResult:Set[ClusterHash] = cpm.findCPMCluster2(cpm.getCliques2()).toSet

    println(cpm.getClusterInterSectNode(clusterResult))

    /*
    * use retain to remove the empty cluster in clusterResult
    * and flatMap to make it
    * */
    /*Ok(views.html.show2(Json.toJson(clusterResult), clusterResult.retain((k, v) => !v.isEmpty).flatMap(x => Set(x._1)).toSet,
      Json.toJson(graph.vertexSet().asScala.toSet), Json.toJson(cpm.getReadableEdge())))
      */

    Ok(views.html.D3.d3view(clusterResult))
    //Ok(Json.toJson(clusterResult))
    //Ok()
  }
}
