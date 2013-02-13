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

    val dimension = (multiParams("width"), multiParams("height")) match {
      case (Seq(x, _*), Seq(y, _*)) => new Dimension(x.toInt, y.toInt)
      case _ => chart.getMinDimension
    }
    chart.setDimension(dimension)

    val content = new ImageContent(new ImageRenderer {
      def render = (new ChartRenderer(chart)).render
    })

    contentType = content.getContentType
    transformToBytes(content)
  }

  private def transformToBytes(content: ImageContent) = {
  	val byteStream = new ByteArrayOutputStream()
    content.write(byteStream)
    byteStream.toByteArray
  }


}
