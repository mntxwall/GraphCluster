package controllers

import javax.inject.Inject
import play.api.Logger
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}


case class Logging[A](action: Action[A]) extends Action[A] {

  def apply(request: Request[A]): Future[Result] = {
    //Logger.info("Calling action")
    println("in extends Action")
    action(request)
  }

  override def parser = action.parser
  override def executionContext = action.executionContext
}

class CustomAction @Inject() (parser: BodyParsers.Default)(implicit ec: ExecutionContext) extends ActionBuilderImpl(parser) {
  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    //Logger.info("Calling action")
    println("Call action")
    block(request)
  }
  override def composeAction[A](action: Action[A]) = new Logging(action)
}