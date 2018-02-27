package controllers

import javax.inject.Inject

import entities.{RestResult, User}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext

class AbstractAuthController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  implicit val userWrites: Writes[User] = (
    (JsPath \ "id").writeNullable[Long] and
    (JsPath \ "userName").write[String] and
    (JsPath \ "password").writeNullable[String]
  )(unlift(User.unapply))

  implicit val userReads: Reads[User]= (
    (JsPath \ "id").readNullable[Long] and
    (JsPath \ "userName").read[String] and
    (JsPath \ "password").readNullable[String]
  )(User.apply _)

  implicit val resultBooleanWrites = Json.writes[RestResult[Boolean]]
  implicit val resultUserWrites = Json.writes[RestResult[User]]
  implicit val resultUserSeqWrites = Json.writes[RestResult[Seq[User]]]

  protected def validateUser = parse.json.validate(v => v.validate.asEither.left.map(e => BadRequest(JsError.toJson(e))))
}
