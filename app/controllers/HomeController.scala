package controllers

import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{Files, Paths}

import javax.inject._
import models.GraphRepository
import play.api._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, graphRepository: GraphRepository) extends AbstractController(cc) {

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

    request.body.file("graph").map{ x =>
      val filename = x.filename

      val fileWithPath:String = s"/tmp/fileUploads/$filename"

      x.ref.atomicMoveWithFallback(Paths.get(fileWithPath))

      Files.setPosixFilePermissions(Paths.get(fileWithPath),
        PosixFilePermissions.fromString("rw-r--r--"))

      Ok("Upload Success")

    }.getOrElse(Ok("Upload Error"))

  }

  def check(params: String) = Action{implicit request =>
    Logger.debug(params)
    //val arrayParams: Array[String] = params.split("=")

    val tableName: String = params.split("=")(1)

    val tableResult: Int = graphRepository.checkTableExist(tableName)
    //graphRepository.createTable(tableName)
    //if table is not exist create table
    if(tableResult == 0){

      graphRepository.createTable(tableName)
    }

    val json: JsValue = Json.parse(
      s"""
         {
         "result":$tableResult
         }
      """)

    Ok(json)

  }

}
