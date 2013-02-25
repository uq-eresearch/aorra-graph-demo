import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._
import com.typesafe.sbt.SbtStartScript

object AorraGraphDemoBuild extends Build {
  val Organization = "au.edu.uq"
  val Name = "AORRA Graph Demo"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.10.0"
  val ScalatraVersion = "2.2.0"
  val BatikVersion = "1.7"

  lazy val project = Project (
    "aorra-graph-demo",
    file("."),
    settings = Defaults.defaultSettings ++ seq(SbtStartScript.startScriptForClassesSettings: _*) ++ ScalatraPlugin.scalatraWithJRebel ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalatest" % "2.2.0" % "test",
        "org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" % "test",
        "ch.qos.logback" % "logback-classic" % "1.0.6" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "compile;container",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "compile;container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar")),
        "commons-io" % "commons-io" % "2.3",
        "org.jfree" % "jfreechart" % "1.0.14",
        "org.apache.commons" % "commons-lang3" % "3.1",
        "com.google.guava" % "guava" % "12.0",
        "org.apache.xmlgraphics" % "batik-codec" % BatikVersion,
        "org.apache.xmlgraphics" % "batik-rasterizer" % BatikVersion,
        "org.apache.xmlgraphics" % "batik-svggen" % BatikVersion,
        "org.apache.xmlgraphics" % "fop" % "1.0"
      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty,  /* default imports should be added here */
            Seq.empty,  /* add extra bindings here */
            Some("templates")
          )
        )
      },
      mainClass in Compile := Some("JettyLauncher")
    )
  ).settings(net.virtualvoid.sbt.graph.Plugin.graphSettings: _*)

}
