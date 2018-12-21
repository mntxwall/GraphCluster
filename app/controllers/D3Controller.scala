package controllers

import javax.inject.{Inject, Singleton}
import models.{CPMRepository, ClusterHash, D3GraphNodes, GraphRepository}
import org.jgrapht.graph.DefaultEdge
import play.api.libs.json._
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

    //val clusterResult:Set[ClusterHash] = cpm.findCPMCluster2(cpm.getCliques2()).toSet

   // println(clusterResult)

    Ok(Json.obj("nodes" -> gv, "links" -> gl))

  }

  def edges() = Action{



    Ok(Json.toJson(""))
  }

  def index() = Action{


    val cpm = new CPMRepository(graphRepository)
    val graph = cpm.CreateGraph("webs")
    val clusterResult:Set[ClusterHash] = cpm.findCPMCluster2(cpm.getCliques2()).toSet

    val mn = mutable.Set[String]()

    println(cpm.getClusterInterSectNode(clusterResult))

    val gv = graph.vertexSet().asScala.map{x =>

      Json.obj("id" -> x)
    }


    val gl = graph.edgeSet().asScala.map{x: DefaultEdge =>
      Json.obj("source" -> graph.getEdgeSource(x), "target" -> graph.getEdgeTarget(x))
      //cliqueHashMap.apply(2) += setVertex
    }

    val tt = clusterResult.map{x =>

      //println("x is " + x)

      val l = x.clusterSet.flatMap{y =>

        println("y is " + y)
        y.flatMap{z =>

          println("z is " + z)
          graph.edgeSet().asScala.map{t:DefaultEdge =>

            val e1 = graph.getEdgeSource(t)
            val e2 = graph.getEdgeTarget(t)

            if(y.contains(e1) && y.contains(e2) && (e1 == z || e2 == z)){
            //if ( (e1 == z && y.contains(e1)) || (e2 == z) && (y.contains(e2))){

              mn.add(graph.getEdgeSource(t))
              mn.add(graph.getEdgeTarget(t))
              Json.obj("source" -> graph.getEdgeSource(t), "target" -> graph.getEdgeTarget(t))
            }
            else JsNull
          }.filter(p => p != JsNull)
        }
      }
      val n = x.clusterSet.zipWithIndex.flatMap{case(y, j) =>
        println("y is " + y)
        //val t = y.map(z => Json.obj("id" -> z))

        val m = y.map{z => Json.obj("id" -> z, "group" -> j)}

        println("m is " + m)

        m

      }
      println(l)
      Json.obj("clique"-> x.clique, "links" -> l, "nodes" -> n)
      //Map("clique" -> x.clique, "cluster" -> n)

    }

    //println(mn)
    //println(tt)

    /*
    * use retain to remove the empty cluster in clusterResult
    * and flatMap to make it
    * */
    /*Ok(views.html.show2(Json.toJson(clusterResult), clusterResult.retain((k, v) => !v.isEmpty).flatMap(x => Set(x._1)).toSet,
      Json.toJson(graph.vertexSet().asScala.toSet), Json.toJson(cpm.getReadableEdge())))
      */

    println(clusterResult)
    Ok(views.html.D3.d3view(clusterResult, Json.obj("nodes" -> gv, "links" -> gl, "group" -> tt)))
    //Ok(Json.toJson(clusterResult))
    //Ok()
  }
}
