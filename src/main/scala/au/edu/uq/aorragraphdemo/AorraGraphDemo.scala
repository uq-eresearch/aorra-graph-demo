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
    val runtime = Map(
        "name" -> System.getProperty("java.runtime.name"),
        "version" -> System.getProperty("java.runtime.version"))
    contentType = "text/html"
    mustache("/index", "runtime" -> runtime)
  }

  get("/sugarcane-practice-chart") {
    val nutrients = "Nutrients";
    val herbicides = "Herbicides";
    val soil = "Soil";

    val builder = new CategoryDatasetBuilder()
    builder
    .addValue(21.0, "A_08", nutrients)
    .addValue(19.0, "B_08", nutrients)
    .addValue(37.0, "C_08", nutrients)
    .addValue(23.0, "D_08", nutrients)
    .addValue(21.0, "A_09", nutrients)
    .addValue(28.0, "B_09", nutrients)
    .addValue(32.0, "C_09", nutrients)
    .addValue(19.0, "D_09", nutrients);

    builder
    .addValue(28.0, "A_08", herbicides)
    .addValue(50.0, "B_08", herbicides)
    .addValue(16.0, "C_08", herbicides)
    .addValue( 6.0, "D_08", herbicides)
    .addValue(23.0, "A_09", herbicides)
    .addValue(53.0, "B_09", herbicides)
    .addValue(20.0, "C_09", herbicides)
    .addValue( 4.0, "D_09", herbicides);

    builder
    .addValue(30.0, "A_08", soil)
    .addValue(40.0, "B_08", soil)
    .addValue(19.0, "C_08", soil)
    .addValue(11.0, "D_08", soil)
    .addValue(27.0, "A_09", soil)
    .addValue(35.0, "B_09", soil)
    .addValue(29.0, "C_09", soil)
    .addValue(09.0, "D_09", soil);

    val dataset = builder.get()
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
    def paramOption(k: String) = {
      try {
        multiParams(k).headOption map { _.toInt }
      } catch {
        case e: NumberFormatException => None
      }
    }
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
