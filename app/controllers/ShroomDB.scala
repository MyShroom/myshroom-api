package controllers

// PLAY
import play.api.libs.json._
import play.api.mvc.Controller
import play.modules.reactivemongo._
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._

// LOCAL
import models.Mushroom
import models.Mushrooms._

// OTHER
import javax.inject.Inject
import reactivemongo.api.ReadPreference._
import scala.concurrent.{ExecutionContext, Future}

// --- //

class ShroomDB @Inject() (val reactiveMongoApi: ReactiveMongoApi)
  extends Controller with MongoController with ReactiveMongoComponents {

  def collection: JSONCollection = db.collection[JSONCollection]("shrooms")

  def jsonQuery(
    json: JsObject
  )(implicit ec: ExecutionContext): Future[Seq[Mushroom]] = {
    collection.find(json)
      .cursor[Mushroom](readPreference = nearest)
      .collect[Seq]()
  }

  /* There should only ever be one result, as Latin names are unique */
  def byLatin(
    latin: String
  )(implicit ec: ExecutionContext): Future[Option[Mushroom]] = {
    collection.find(Json.obj("latin" -> latin)).one[Mushroom]
  }

  def byLatins(
    latins: Seq[String]
  )(implicit ec: ExecutionContext): Future[Seq[Mushroom]] = {
    jsonQuery(Json.obj("latin" -> Json.obj("$in" -> latins)))
  }
}
