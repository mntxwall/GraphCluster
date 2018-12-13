package controllers

import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{Files, Paths}
import java.time.{LocalDate, LocalDateTime}

import javax.inject._
import models.{CPMRepository, Edge, GraphRepository}
import play.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._
import java.time.format.DateTimeFormatter

import org.jgrapht.graph.DefaultEdge

import collection.JavaConverters._
import scala.collection.immutable.HashMap
import scala.collection.mutable

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents,
                               graphRepository: GraphRepository) extends AbstractController(cc) {

  //default value means false
  val CREATE_TABLE_RESULT: Int = 1


  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def hello() = Action(parse.multipartFormData){ implicit request =>

    //println()//.foreach(println)

   // Ok(Json.parse(s"""{"upresult":1 }"""))

    //request.body.dataParts
    request.body.file("graph").map{ x =>

      //val date =
      //val formatter =
      //val text = LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))


      //val filename = x.filename  + s".${x.filename.split(".")(1)}"
      val filename = LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
      val fileWithPath:String = s"/tmp/fileUploads/$filename"
      x.ref.atomicMoveWithFallback(Paths.get(fileWithPath))

      Files.setPosixFilePermissions(Paths.get(fileWithPath),
        PosixFilePermissions.fromString("rw-r--r--"))

      //copy the value into the table
      //request.body.dataParts.get("tablename") get the value in the url
      //val aaa =
      graphRepository.insertFromFile(fileWithPath, request.body.dataParts("tablename").head)

      Ok(Json.parse(s"""{"upresult":0 }"""))

    }.getOrElse(Ok(Json.parse(s"""{"upresult":1 }""")))

  }

  def show(tb: Option[String]) = Action{

    //checkTableExist return 1 means the table is exist
    if(graphRepository.checkTableExist(tb.get) == 1){

      //val gVertexSet = graphRepository.getVertex(tb.get)

      //println(aa)
      //val aa = graphRepository.getEdges(tb.get)
      //println(aa)

      //val bb = List[Set[String]](Set("1", "2", "3"), Set("pear", "bb", "cc"))
      //val aa = HashMap("vertex" -> graphRepository.getVertex(tb.get), "edges" -> graphRepository.getEdges(tb.get))

      val cpm = new CPMRepository(graphRepository)
      val graph = cpm.CreateGraph(tb.get)
      val clusterResult = cpm.findCPMCluster(cpm.getCliques())
      println(clusterResult)

      val gVertexSet = graph.vertexSet().asScala

      //val aaaa = gVertexSet.toSeq

      //val bbb = mutable.Set[String](aaaa: _*)
      //找出各子集的相交点
      /*
      * 找出各子集的相交点
      * 也就是多个集合相交的问题
      * 记集合为{m1, m2, m3, ..., mn}
      * 要计算集合中的共同值，可以这么做
      * 取n=m1, E1= {m2,...., mn}
      * 做n与E1中所有元素的交集，记为nmi,nm2表示n与m2的交集
      * 取nm2 nm3 nm4 ...nmn的并集，记为nE1的交集。
      * 以此类推，取n=m2, E2={m3, m4, .... mn}的结果集记为nE2
      * 直到n=mn-1, En-1={mn}时，计算结束。
      * 取nE1-nEn-1结果的果的并
      * *如果子集的个数为0或是1，则表示该子集没有交集点，记为空交点集
      *
      * */

      /*val clusterResult2:HashMap[String, List[Set[Int]]] = HashMap(
        "5" -> List(Set(1, 2, 3, 4, 5,6), Set(7, 8, 9, 10, 11), Set(1, 3, 10, 13), Set(7, 9))
      )*/
      val cstIntersets = clusterResult.flatMap{ x =>
        //mutable.Set[String]()
        //println(x._2.size)
        if(!x._2.isEmpty && x._2.size >= 2){
          //collection.mutable.ListBuffer(m.toSeq: _*)

          var hSet = x._2
          var mSet2 = mutable.Set[String]()

          var mSet: mutable.Set[String] = hSet(0)
          hSet = hSet.drop(1)

          while(!hSet.isEmpty){

            mSet2 = mSet2 ++ hSet.flatMap{cliqueSet =>

              cliqueSet.intersect(mSet)

            }

            //println(mSet2)

            mSet = hSet(0)
            hSet = hSet.drop(1)
          }

          println(mSet2)

          /*
          while(hSet.size >= 2){
            mSet = mSet ++ hSet.fold(gVertexSet){(z, f) => z.intersect(f)}
            hSet = hSet.drop(1)
          }*/

          /*HashMap(x._1 -> x._2.foldLeft(gVertexSet) { (z, f) =>
            z.intersect(f)
          })*/

          HashMap(s"clique${x._1}" -> mSet2)

        }
        else
          HashMap(s"clique${x._1}" -> Set[String]())
      }

      println(cstIntersets)


      //val aa = Set("1")
      //Json.toJson(cstIntersets)

      Ok(views.html.show2(Json.toJson(clusterResult), clusterResult.retain((k, v) => !v.isEmpty).flatMap(x => Set(x._1)).toSet,
      Json.toJson(gVertexSet.toSet), Json.toJson(cpm.getReadableEdge()), Json.toJson(cstIntersets)))

      //println(cpm.CreateGraph(tb.get).edgeSet())

     // Ok(views.html.show(Json.obj("vertex" -> Json.toJson(graphRepository.getVertex(tb.get)),
      //  "edges" -> Json.toJson(graphRepository.getEdges(tb.get)))))

      //Ok(views.html.show(tb.get))
    }
    else
    {
      Ok(views.html.show(Json.toJson("")))
    }
  }

  def test = Action{


    val cpm = new CPMRepository(graphRepository)
    cpm.CreateGraph()

    val aaa = cpm.getCliques2()

    //println(cpm.findCPMCluster2(aaa))
    println(aaa)

    Ok(Json.toJson(""))
  }

  def cpmHello = Action{

    val cpm = new CPMRepository(graphRepository)
    //println(aa.CreateGraph())
    val graph = cpm.CreateGraph()

    val hellSet = Set(Set(1, 2, 3), Set(4, 5, 6), Set(1, 3, 4))
    var hSet = hellSet

    var mSet:Set[Int] = Set[Int]()

    //hSet = hSet.drop(1)

    //println(hellSet)
    //println(hSet)

    while(!hSet.isEmpty){

      mSet = mSet ++ hSet.fold(Set(1, 2, 3, 4,5,6)){(z, f) => z.intersect(f)}
      hSet = hSet.drop(1)
    }

    println(mSet)
   // println(hellSet.fold(Set(1, 2, 3, 4,5,6)){(z, f) => z.intersect(f)})


   // val aa  = graph.edgeSet().asScala.toSet

    //Json.toJson(graph.vertexSet().asScala.toSet)
    //cpm.getReadableEdge()
    //println(cpm.getReadableEdge())

    val clusterResult = cpm.findCPMCluster(cpm.getCliques())
    //val clickIndexSet = mutable.Set[Int]()

    //println(aa.findCPMCluster(aa.getCliques()))
    //Ok(Json.toJson(cpm.findCPMCluster(cpm.getCliques())))
    //Ok(views.html.show2(clusterResult))


   // val clickIndexSet2 =

   // println(clickIndexSet2)


/*
    clusterResult.keys.foreach{ x =>
      if (!clusterResult.apply(x).isEmpty){
        clickIndexSet.add(x)
      }
    }
    println(clickIndexSet)*/

    /*
    * use retain to remove the empty cluster in clusterResult
    * and flatMap to make it
    * */
    Ok(views.html.show2(Json.toJson(clusterResult), clusterResult.retain((k, v) => !v.isEmpty).flatMap(x => Set(x._1)).toSet,
    Json.toJson(graph.vertexSet().asScala.toSet), Json.toJson(cpm.getReadableEdge())))
  }

  def check(tb: Option[String]) = Action{implicit request =>

    val tableName = tb.get

    var checkResult:Int = CREATE_TABLE_RESULT

   // (request.body \ "hello").as[String]

    //val tableResult: Int = graphRepository.checkTableExist(tableName)
    //graphRepository.createTable(tableName)
    //if table is not exist create table
    if(graphRepository.checkTableExist(tableName) == 0){
      checkResult = graphRepository.createTable(tableName)
    }
   // val json: JsValue =
    Ok(Json.parse(s"""{"result":$checkResult }"""))

  }

  def test2() = Action{

    Ok("okooook")
  }

}
