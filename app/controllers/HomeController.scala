package controllers

import java.nio.file.attribute.PosixFilePermissions
import java.nio.file.{Files, Paths}

import javax.inject._
import play.api._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

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
}
