import com.typesafe.sbt.packager.docker._

name := "araneae.cluster"

organization := "paloul"

scalaVersion := "2.13.8"

exportJars := true

Global / fork := true

Global / connectInput := true

Global / cancelable := false

// The [info] prefixes cause line wrap to get messed up, disable it
Global / outputStrategy := Some(StdoutOutput)

Global / excludeLintKeys += lintUnused

Global / onChangedBuildSource := ReloadOnSourceChanges

// If set to false, sbt-assembly will parallelize JAR creation for faster performance
ThisBuild / assemblyRepeatableBuild := false

scalacOptions ++= Seq(
  "-encoding", "utf8", // Option and arguments on same line
  "-Xfatal-warnings",  // New lines for each options
  "-deprecation",
  "-unchecked",
  "-Ymacro-annotations",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)

// Enable Plugins
enablePlugins(DockerPlugin)
enablePlugins(AkkaGrpcPlugin)
enablePlugins(JavaAppPackaging)

libraryDependencies ++= {
  val AkkaVersion = "2.8.2"
  val AkkaHttpVersion = "10.5.2"
  val AkkaManagementVersion = "1.4.1"

  Seq(
    "com.typesafe.akka"             %% "akka-actor"                             % AkkaVersion,
    "com.typesafe.akka"             %% "akka-actor-typed"                       % AkkaVersion,

    "com.typesafe.akka"             %% "akka-cluster-typed"                     % AkkaVersion,
    "com.typesafe.akka"             %% "akka-cluster-sharding-typed"            % AkkaVersion,

    "com.typesafe.akka"             %% "akka-http"                              % AkkaHttpVersion,
    "com.typesafe.akka"             %% "akka-http-spray-json"                   % AkkaHttpVersion,

    "com.typesafe.akka"             %% "akka-cluster-tools"                     % AkkaVersion,

    // Discovery for cloud deployment auto discovery capabilities
    // https://doc.akka.io/docs/akka-management/current/discovery/index.html
    // https://doc.akka.io/docs/akka-management/current/discovery/aws.html#project-info
    "com.typesafe.akka"             %% "akka-discovery"                         % AkkaVersion,
    "com.lightbend.akka.discovery"  %% "akka-discovery-kubernetes-api"          % AkkaManagementVersion,
    "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap"      % AkkaManagementVersion,

    // Logging support, logback is an option, using scribe instead with slf4j plugin
    // https://logback.qos.ch/manual/configuration.html
    // https://github.com/outr/scribe
    //"ch.qos.logback"                % "logback-classic"                         % "1.2.11",
    //"net.logstash.logback"          % "logstash-logback-encoder"                % "7.2",
    "com.outr"                      %% "scribe-slf4j"                           % "3.11.9"
  )
}

// Define Docker Plugin settings
Docker / packageName := name.value
Docker / version := version.value
dockerBaseImage := "eclipse-temurin:17.0.4_8-jre-alpine"
dockerExposedPorts := Seq(5000, 2550, 8558)
dockerCommands ++= Seq( // Add custom Docker commands to the Dockerfile
  Cmd("USER", "root"), // Switch to root to allow apt-get upgrade command
  ExecCmd("RUN", "apk", "update"),
  ExecCmd("RUN", "apk", "upgrade", "--latest"),
  Cmd("USER", (Docker / daemonUser).value) // Switch back to default user from root
)