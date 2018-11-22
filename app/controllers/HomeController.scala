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

    Ok(Json.parse(s"""{"upresult":1 }"""))

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

      //val aa = graphRepository.getVertex(tb.get)

      //println(aa)
      //val aa = graphRepository.getEdges(tb.get)
      //println(aa)

      //val bb = List[Set[String]](Set("1", "2", "3"), Set("pear", "bb", "cc"))
      //val aa = HashMap("vertex" -> graphRepository.getVertex(tb.get), "edges" -> graphRepository.getEdges(tb.get))

      Ok(views.html.show(Json.obj("vertex" -> Json.toJson(graphRepository.getVertex(tb.get)),
        "edges" -> Json.toJson(graphRepository.getEdges(tb.get)))))

      //Ok(views.html.show(tb.get))
    }
    else
    {
      Ok(views.html.show(Json.toJson("")))
    }
  }

  def cpmHello = Action{

    val cpm = new CPMRepository
    //println(aa.CreateGraph())
    val graph = cpm.CreateGraph()


   // val aa  = graph.edgeSet().asScala.toSet

    //Json.toJson(graph.vertexSet().asScala.toSet)
    //cpm.getReadableEdge()
    println(cpm.getReadableEdge())

    val clusterResult = cpm.findCPMCluster(cpm.getCliques())
    val clickIndexSet = mutable.Set[Int]()

    //println(aa.findCPMCluster(aa.getCliques()))
    //Ok(Json.toJson(cpm.findCPMCluster(cpm.getCliques())))
    //Ok(views.html.show2(clusterResult))
    clusterResult.keys.foreach{ x =>
      if (!clusterResult.apply(x).isEmpty){
        clickIndexSet.add(x)
      }
    }

    Ok(views.html.show2(Json.toJson(clusterResult), clickIndexSet)
    (Json.toJson(graph.vertexSet().asScala.toSet), Json.toJson(cpm.getReadableEdge())))
  }



  def check(tb: Option[String]) = Action{implicit request =>

    val tableName = tb.get

    var checkResult:Int = CREATE_TABLE_RESULT

    //val tableResult: Int = graphRepository.checkTableExist(tableName)
    //graphRepository.createTable(tableName)
    //if table is not exist create table
    if(graphRepository.checkTableExist(tableName) == 0){
      checkResult = graphRepository.createTable(tableName)
    }
   // val json: JsValue =
    Ok(Json.parse(s"""{"result":$checkResult }"""))

  }

}
