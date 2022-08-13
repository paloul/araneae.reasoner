import com.typesafe.sbt.packager.docker._

name := "araneae.cluster"

organization := "paloul"

scalaVersion := "2.13.8"

exportJars := true

Global / cancelable := false

Global / excludeLintKeys += lintUnused

Global / onChangedBuildSource := ReloadOnSourceChanges

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
enablePlugins(GraalVMNativeImagePlugin)

libraryDependencies ++= {
  val AkkaVersion = "2.6.19"
  val AkkaHttpVersion = "10.2.9"
  val AkkaManagementVersion = "1.1.3"

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

    // Logging support, using logback
    // https://logback.qos.ch/manual/configuration.html
    "ch.qos.logback"                % "logback-classic"                         % "1.2.11",
    "net.logstash.logback"          % "logstash-logback-encoder"                % "7.2",

    // Apache Lucene
    // https://lucene.apache.org/
    // https://search.maven.org/artifact/org.apache.lucene/lucene-core
    "org.apache.lucene"             % "lucene-core"                             % "9.3.0",

    // MapDB
    // https://mapdb.org/
    // https://github.com/jankotek/mapdb/
    "org.mapdb"                     % "mapdb"                                   % "3.0.8"

  )
}

assembly / assemblyMergeStrategy  := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case x => MergeStrategy.first
}

// Define GraalVM Native Image Options
graalVMNativeImageOptions ++= Seq(
  "-H:ResourceConfigurationFiles=../../configs/resource-config.json",
  "-H:ReflectionConfigurationFiles=../../configs/reflect-config.json",
  "-H:JNIConfigurationFiles=../../configs/jni-config.json",
  "-H:DynamicProxyConfigurationFiles=../../configs/proxy-config.json"
)

// Define Docker Plugin settings
Docker / packageName := name.value
Docker / version := version.value
dockerBaseImage := "openjdk:11-jre-slim-bullseye"
dockerExposedPorts := Seq(5000, 2550, 8558)
dockerCommands ++= Seq( // Add custom Docker commands to the Dockerfile
  Cmd("USER", "root"), // Switch to root to allow apt-get upgrade command
  ExecCmd("RUN", "apt-get", "update"),
  ExecCmd("RUN", "apt-get", "upgrade", "-y"),
  ExecCmd("RUN", "apt-get", "dist-upgrade", "-y"),
  Cmd("USER", (Docker / daemonUser).value) // Switch back to default user from root
)