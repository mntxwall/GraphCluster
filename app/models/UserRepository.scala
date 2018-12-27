package models

import javax.inject.Inject
import play.api.db.DBApi

class UserRepository @Inject()(dbApi: DBApi){
  private val db = dbApi.database("default")


}
