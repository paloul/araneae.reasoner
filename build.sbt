import com.typesafe.sbt.packager.docker._

name := "araneae.reasoner"

organization := "paloul"

scalaVersion := "2.13.6"

exportJars := true

Global / cancelable := false

Global / onChangedBuildSource := ReloadOnSourceChanges

scalacOptions ++= Seq(
  "-encoding", "utf8", // Option and arguments on same line
  "-Xfatal-warnings",  // New lines for each options
  "-deprecation",
  "-unchecked",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)

libraryDependencies ++= {
  val AkkaVersion = "2.6.16"
  val AkkaHttpVersion = "10.2.6"
  val AkkaManagementVersion = "1.1.1"

  Seq(
    "com.typesafe.akka"             %% "akka-actor-typed"             % AkkaVersion,

    "com.typesafe.akka"             %% "akka-serialization-jackson"   % AkkaVersion,

    "com.typesafe.akka"             %% "akka-cluster-typed"           % AkkaVersion,
    "com.typesafe.akka"             %% "akka-cluster-sharding-typed"  % AkkaVersion,

    // Discovery for cloud deployment auto discovery capabilities
    // https://doc.akka.io/docs/akka-management/current/discovery/index.html
    // https://doc.akka.io/docs/akka-management/current/discovery/aws.html#project-info
    "com.typesafe.akka"             %% "akka-discovery"                     % AkkaVersion,
    "com.lightbend.akka.discovery"  %% "akka-discovery-kubernetes-api"      % AkkaManagementVersion,
    "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap"  % AkkaManagementVersion,

    "com.typesafe.akka"             %% "akka-stream"                        % AkkaVersion,
    "com.typesafe.akka"             %% "akka-http"                          % AkkaHttpVersion,
    "com.typesafe.akka"             %% "akka-http-spray-json"               % AkkaHttpVersion,

    // Logging support, using logback
    // https://logback.qos.ch/manual/configuration.html
    "ch.qos.logback"                % "logback-classic"                     % "1.2.5",
    "net.logstash.logback"          % "logstash-logback-encoder"            % "6.6"

  )
}

// Enable the Docker Plugin and define settings
enablePlugins(DockerPlugin)
Docker / packageName := name.value
Docker / version := version.value
Docker / dockerBaseImage := "openjdk:11-jre-slim-bullseye"
Docker / dockerExposedPorts := Seq(5000, 2550, 8558)

// Add custom Docker Cmds to the Dockerfile
dockerCommands ++= Seq(
  Cmd("USER", "root"),
  ExecCmd("RUN", "apt-get", "update"),
  ExecCmd("RUN", "apt-get", "upgrade", "-y"),
  ExecCmd("RUN", "apt-get", "dist-upgrade", "-y"),
  Cmd("USER", (Docker / daemonUser).value)
)