name := "ftm"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

resolvers += "Maven Repository" at "http://mvnrepository.com/artifact/"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "org.postgresql" % "postgresql" % "9.3-1101-jdbc41",
  "org.webjars" % "bootstrap" % "3.2.0"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
