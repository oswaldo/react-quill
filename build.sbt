import sbt.Project.projectToRef

lazy val clients = Seq(scalajsclient)
lazy val scalaV = "2.11.8"

lazy val playserver = (project in file("play")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := clients,
  libraryDependencies ++= Seq(
    specs2 % Test
  )
).enablePlugins(PlayScala).
  aggregate(clients.map(projectToRef): _*).
  dependsOn(sharedJvm)

lazy val scalajsclient = (project in file("scalajs")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value)
).enablePlugins(ScalaJSPlugin, ScalaJSPlay).
  dependsOn(sharedJs)

lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared")).
  settings(
    scalaVersion := scalaV,
    
    //added resolvers because of silhouette
    resolvers ++= Seq(
      "Atlassian Releases" at "https://maven.atlassian.com/public/",
      "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
      "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
    )
    
  ).
  jvmSettings(
    libraryDependencies ++= Seq(
      specs2 % Test,
      "org.webjars" % "jquery" % "3.0.0",
      jdbc, evolutions,
      "de.leanovate" %% "play-cassandra-evolutions" % "2.5.0",
      "io.getquill" %% "quill-cassandra" % "0.7.0",
      "com.lihaoyi" %% "scalatags" % "0.5.5",
      "com.github.japgolly.scalacss" %% "ext-scalatags" % "0.4.1",
      "com.github.japgolly.scalacss" %% "core" % "0.4.1",
      "com.lihaoyi" %% "upickle" % "0.4.1",
      "com.mohiva" %% "play-silhouette" % "4.0.0-RC1",
      "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0-RC1",
      "com.mohiva" %% "play-silhouette-persistence" % "4.0.0-RC1",
      "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0-RC1",
      "com.mohiva" %% "play-silhouette-testkit" % "4.0.0-RC1" % "test"
    )
  ).
  jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.8.2",
      "com.lihaoyi" %%% "scalatags" % "0.5.5",
      "com.lihaoyi" %%% "upickle" % "0.4.1",
      "com.github.japgolly.scalajs-react" %%% "core" % "0.11.1",
      "com.github.japgolly.scalajs-react" %%% "extra" % "0.11.1",
      "com.github.japgolly.scalajs-react" %%% "ext-monocle" % "0.11.1",
      "com.github.japgolly.scalacss" %%% "core" % "0.4.1",
      "com.github.japgolly.scalacss" %%% "ext-scalatags" % "0.4.1",
      "com.github.japgolly.scalacss" %%% "ext-react" % "0.4.1"
    ),
    jsDependencies ++= Seq(
      "org.webjars.npm" % "react"     % "0.14.2" / "react-with-addons.js" commonJSName "React"    minified "react-with-addons.min.js",
      "org.webjars.npm" % "react-dom" % "0.14.2" / "react-dom.js"         commonJSName "ReactDOM" minified "react-dom.min.js" dependsOn "react-with-addons.js"
    )
  ).
  jsConfigure(_ enablePlugins ScalaJSPlay)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the Play project at sbt startup
onLoad in Global := (Command.process("project playserver", _: State)) compose (onLoad in Global).value

// for Eclipse users
EclipseKeys.skipParents in ThisBuild := false


fork in run := true
