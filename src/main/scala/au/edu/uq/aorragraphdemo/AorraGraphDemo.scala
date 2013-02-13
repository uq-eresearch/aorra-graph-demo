package au.edu.uq.aorragraphdemo

import org.scalatra._
import scalate.ScalateSupport

import java.awt.Dimension
import java.io.ByteArrayOutputStream

import ereefs.content.Content
import ereefs.charts._
import ereefs.images._

class AorraGraphDemo extends ScalatraFilter with ScalateSupport {

  get("/") {
    var progressChartUrl = relativeUrl("progress-chart", Map(
      "tl" -> "Horticulture",
      "tr" -> "2013 targets",
      "value" -> "42"
    ))
    <html>
	  <head>
	  	<title>AORRA Graph Demo</title>
	  </head>
	  <body>
    	<h1>Chart Examples</h1>
	  	<ul>
	  	  <li><a href={progressChartUrl}>Progress Chart</a></li>
	  	  <li><a href="sugarcane-practice-chart">Sugar Practice Chart</a></li>
	  	</ul>
	  </body>
	</html>
  }

  get("/sugarcane-practice-chart") {
    val dsBuilder = new CategoryDatasetBuilder()
    Seq("A","B","C","D") foreach { l =>
	  Seq("08","09") foreach { n =>
        Seq("Nutrients", "Herbicides", "Soil") foreach { category =>
          val series = s"${l}_${n}"
          dsBuilder.addValue(25.0, series, category)
        }
      }
    }
    val dataset = dsBuilder.get()
    val chart = ChartFactory.getSugarcanePracticeChart(
        new Dimension(500, 500),
        dataset)

    val content = new ChartContentWrapper(chart)
    contentType = content.getContentType
    transformToBytes(content)

  }

  get("/progress-chart") {
    val builder = new ProgressChartBuilder(
        multiParams("tl").headOption getOrElse "",
        multiParams("tr").headOption getOrElse "",
        multiParams("max").headOption map { _.toInt} getOrElse 100)
    val chart = builder.get(multiParams("value").headOption match {
      case Some(v) => v.toFloat
      case None => halt(400, """Parameter "value" is required.""")
    })
    chart.setDimension(chart.getMinDimension)

    val renderer = new ChartRenderer(chart)
    renderer.setScale(rendererScale(chart.getDimension, multiParams))

    val content = chartImageContent(renderer)
    contentType = content.getContentType
    transformToBytes(content)
  }

  private def chartImageContent(renderer: ChartRenderer) = {
    new ImageContent(new ImageRenderer {
      def render = renderer.render
    })
  }

  private def rendererScale(d: Dimension, multiParams: MultiParams) = {
    def paramOption(k: String) = multiParams(k).headOption map { _.toInt }
    new Dimension(
        paramOption("width") getOrElse d.getWidth.toInt,
        paramOption("height") getOrElse d.getHeight.toInt)
  }

  private def transformToBytes(content: Content) = {
  	val byteStream = new ByteArrayOutputStream()
    content.write(byteStream)
    byteStream.toByteArray
  }


}
