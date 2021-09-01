name := "araneae.reasoner"

organization := "paloul"

scalaVersion := "2.13.6"

exportJars := true

Global / cancelable := false

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