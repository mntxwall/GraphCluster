package controllers

import java.util.Properties

import javax.inject.Inject
import javax.naming.{Context, NamingEnumeration}
import javax.naming.directory.{InitialDirContext, SearchControls, SearchResult}
//import org.omg.CosNaming.NamingContextPackage.NotFound
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class UserRequest[A](val username: Option[String], request: Request[A]) extends WrappedRequest[A](request)




class UserAction @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[UserRequest, AnyContent] with
    ActionTransformer[Request, UserRequest]{

 /* def transform[A](request: Request[A]) = Future.successful {

  }*/
 override protected def transform[A](request: Request[A]): Future[UserRequest[A]] = {
   Future.successful(new UserRequest(Option("HelloHello"), request))
 }




  def validateForLDAP(username: String, passcode: String): Boolean = {

    val result = Try {
      var props = new Properties
      props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
      props.put(Context.PROVIDER_URL, "ldap://192.168.1.121:389")
      props.put(Context.SECURITY_PRINCIPAL, s"cn=$username,cn=staff,dc=myreutore,dc=local")
      props.put(Context.SECURITY_CREDENTIALS, "administrator")

      var context: InitialDirContext = new InitialDirContext(props)

      val controls: SearchControls = new SearchControls
      controls.setReturningAttributes(Array[String]("givenName", "sn", "memberOf", "cn"))
      controls.setSearchScope(SearchControls.SUBTREE_SCOPE)

      val answers: NamingEnumeration[SearchResult] = context.search("dc=myrtor,dc=local", s"cn=$username", controls)
      val result: SearchResult = answers.nextElement

      val user: String = result.getNameInNamespace
      props = new Properties
      props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory")
      props.put(Context.PROVIDER_URL, "ldap://192.168.1.121:389")
      props.put(Context.SECURITY_PRINCIPAL, user)
      props.put(Context.SECURITY_CREDENTIALS, passcode)
      context = new InitialDirContext(props)
    }
    result match {
      case Success(v) => true
      case Failure(v) => false
    }

  }
/*
  def filter[A](request: UserRequest[A]) = Future.successful {
    //block(request)
    Some(Results.Forbidden(requ))
  }*/

}




