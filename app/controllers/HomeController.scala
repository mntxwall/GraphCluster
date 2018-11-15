package controllers

import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{Files, Paths}
import java.time.{LocalDate, LocalDateTime}

import javax.inject._
import models.GraphRepository
import play.api._
import play.api.libs.json.{JsValue, Json}
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

      graphRepository.insertFromFile(fileWithPath, request.body.dataParts.get("tablename").get.apply(0))

      Ok(Json.parse(s"""{"upresult":0 }"""))

    }.getOrElse(Ok(Json.parse(s"""{"upresult":1 }""")))

  }



  def check(params: String) = Action{implicit request =>

    Logger.debug(params)
  //  Logger.debug( (System.currentTimeMillis() / 1000L).toString)

    val date = LocalDateTime.now
    val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    val text = date.format(formatter)

    Logger.debug(text)

    //val arrayParams: Array[String] = params.split("=")

    val tableName: String = params.split("=")(1)

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
