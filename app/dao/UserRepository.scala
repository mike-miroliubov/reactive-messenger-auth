package dao

import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import entities.User
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.PostgresProfile

import scala.concurrent.Future

@Singleton
class UserRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit context: DatabaseExecutionContext)
  extends DelegableAuthInfoDAO[PasswordInfo] with HasDatabaseConfigProvider[PostgresProfile] {
  import profile.api._

  private class UserTable(tag: Tag) extends Table[User](tag, "messenger_user") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def password = column[String]("password")

    override def * = (id.?, name, password.?) <> (User.tupled, User.unapply)
  }

  private val users = TableQuery[UserTable]

  def getUser(id: Long): Future[User] = db.run(users.filter(_.id === id).result.head).map(_.hidePassword)

  def findUser(user: User): Future[Option[User]] = db.run {
    users
      .filter(row => row.name === user.userName && row.password === user.password)
      .result
      .headOption
  }

  def findUserByName(userName: String): Future[Option[User]] = db.run {
    users.filter(r => r.name === userName).result.headOption
  }

  def createUser(user: User): Future[User] = {
    val action = (
      for {
        newId <- users.returning(users.map(_.id)) += user
        newUser <- users.filter(_.id === newId).result.head
      } yield newUser
    ).transactionally

    db.run(action).map(_.hidePassword)
  }

  def getUsers(): Future[Seq[User]] = db.run(users.result).map(u => u.map(_.hidePassword)) // just for testing

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    findUserByName(loginInfo.providerKey).map({
      case Some(u) => Some(PasswordInfo("bcrypt", u.password.get, Option(BCrypt.gensalt)))
      case None => None
    })
  }

  override def add(loginInfo: LoginInfo,
                   authInfo: PasswordInfo): Future[PasswordInfo] = ???

  override def update(loginInfo: LoginInfo,
                      authInfo: PasswordInfo): Future[PasswordInfo] = ???

  override def save(loginInfo: LoginInfo,
                    authInfo: PasswordInfo): Future[PasswordInfo] = ???

  override def remove(loginInfo: LoginInfo): Future[Unit] = ???
}
