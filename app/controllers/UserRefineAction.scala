package controllers

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


object ItemDao {
  def findById(id: String) = Some("")
}

class UserRefineAction @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[Request, AnyContent] with ActionRefiner[Request, UserRequest]{

  override protected def refine[A](request: Request[A]) = {

    val result = request.session.get("username")
      .map(user => new UserRequest[A](Option(user), request))
      .toRight(Results.Ok(views.html.login()))

    //val result = new UserRequest[A](Option("bbbb"), request)
    Future.successful{
      result
    }

  }

  def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]) = {
    block(request)
  }
}
