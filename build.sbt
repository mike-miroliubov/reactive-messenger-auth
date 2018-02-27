name := "reactive_messenger_auth"
 
version := "1.0" 
      
lazy val `reactive_messenger_auth` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
resolvers += Resolver.jcenterRepo
      
scalaVersion := "2.12.2"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )
libraryDependencies += "org.postgresql" % "postgresql" % "42.1.1"
libraryDependencies += evolutions // evolutions
libraryDependencies += "com.typesafe.play" %% "play-slick" % "3.0.3"

// Silhouette config
val silhouetteVer = "5.0.0"
lazy val silhouetteLib = Seq(
  "com.mohiva" %% "play-silhouette" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-persistence" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-testkit" % silhouetteVer % "test"
)

libraryDependencies ++= silhouetteLib

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

      