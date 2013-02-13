package au.edu.uq.aorragraphdemo

import org.scalatra._
import scalate.ScalateSupport

import java.awt.Dimension
import java.io.ByteArrayOutputStream

import ereefs.charts._
import ereefs.images._

class AorraGraphDemo extends ScalatraFilter with ScalateSupport {

  get("/") {
    <html>
	  <head>
	  	<title>AORRA Graph Demo</title>
	  </head>
	  <body>
	  	<ul>
	  	  <li><a href="progress-chart?value=42">Progress Bar</a></li>
	  	</ul>
	  </body>
	</html>
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

  private def transformToBytes(content: ImageContent) = {
  	val byteStream = new ByteArrayOutputStream()
    content.write(byteStream)
    byteStream.toByteArray
  }


}
