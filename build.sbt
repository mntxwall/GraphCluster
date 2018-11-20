name := """GraphCluster"""
organization := "com.wei"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala).disablePlugins(PlayFilters)

scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += "org.jgrapht" % "jgrapht-core" % "1.3.0"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"
libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.2"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"