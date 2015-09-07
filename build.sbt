name := "ftm"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

scalacOptions ++= Seq(
	"-unchecked",
	"-deprecation",
	"-feature",
	"-Xfatal-warnings",
	"-Xfuture",
	"-Xlint"
)

resolvers += "Maven Repository" at "http://mvnrepository.com/artifact/"

libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  "com.typesafe.play" %% "anorm" % "2.5.0",
  "org.postgresql" % "postgresql" % "9.4-1202-jdbc42",
  "org.webjars" % "bootstrap" % "3.3.5",
  "org.webjars" % "jquery" % "2.1.4"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
