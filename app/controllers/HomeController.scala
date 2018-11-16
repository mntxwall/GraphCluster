package controllers

import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{Files, Paths}
import java.time.{LocalDate, LocalDateTime}

import javax.inject._
import models.{Edge, GraphRepository}
import play.api._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._
import java.time.format.DateTimeFormatter

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, graphRepository: GraphRepository) extends AbstractController(cc) {

  //default value means false
  val CREATE_TABLE_RESULT: Int = 1


  implicit val edgeWrites: Writes[Edge] = (
    (JsPath \ "vertexa").write[String] and
      (JsPath \ "vertexb").write[String]
    )(unlift(Edge.unapply))


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

      //Ok(Json.obj("vertex" -> Json.toJson(graphRepository.getVertex(tb.get)),
       // "edges" -> Json.toJson(graphRepository.getEdges(tb.get))))

      Ok(views.html.show(tb.get))
    }
    else
    {
      Ok(views.html.show(tb.get))
    }
  }



  def check(tb: Option[String]) = Action{implicit request =>

    //Logger.debug(params)
  //  Logger.debug( (System.currentTimeMillis() / 1000L).toString)

    //val date = LocalDateTime.now
    //val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    //val text = date.format(formatter)

    //Logger.debug(text)

    //val arrayParams: Array[String] = params.split("=")

    //val tableName: String = params.split("=")(1)
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
