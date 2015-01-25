name := "ftm"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

resolvers += "Maven Repository" at "http://mvnrepository.com/artifact/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
  "org.webjars" % "bootstrap" % "3.3.2",
  "org.webjars" % "jquery" % "2.1.3"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
