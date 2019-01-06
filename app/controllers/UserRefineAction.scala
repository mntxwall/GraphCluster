package controllers

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


object ItemDao {
  def findById(id: String) = Some(id)
}

class UserRefineAction @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] with ActionRefiner[UserRequest, UserRequest]{

  override protected def refine[A](request: UserRequest[A]): Future[Either[Result, UserRequest[A]]] = {
    val result = request.session.get("username")
      .flatMap(ItemDao.findById(_))
      .map(user => new UserRequest[A](Option(user), request))
      .toRight(left = Results.Forbidden)
    Future.successful(result)

  }

  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] = {
    block(request)
  }
}
