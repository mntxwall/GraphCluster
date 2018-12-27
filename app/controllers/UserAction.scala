package controllers

import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class UserRequest[A](val username: Option[String], request: Request[A]) extends WrappedRequest[A](request)

class UserAction @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] with
    ActionTransformer[Request, UserRequest]{

  def transform[A](request: Request[A]) = Future.successful {
    new UserRequest(Option("HelloHello"), request)
  }
/*
  def filter[A](request: UserRequest[A]) = Future.successful {
    //block(request)
    Some(Results.Forbidden(requ))
  }*/
}




