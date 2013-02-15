package au.edu.uq.aorragraphdemo

import scala.collection.immutable.ListMap
import org.scalatra._
import scalate.ScalateSupport
import java.awt.Dimension
import java.io.ByteArrayOutputStream
import org.jfree.data.category.CategoryDataset
import org.jfree.chart.title.TextTitle
import au.edu.uq.aorra.charts._
import ereefs.charts.Configuration.TITLE_FONT
import ereefs.content.Content
import ereefs.charts.{
  CategoryDatasetBuilder, ChartContentWrapper, ChartRenderer,
  DimensionsWrapper, ProgressChart, ProgressChartBuilder}
import ereefs.images._
import java.util.NoSuchElementException

class AorraGraphDemo extends ScalatraFilter with ScalateSupport {

  get("/") {
    val runtime = Map(
        "name" -> System.getProperty("java.runtime.name"),
        "version" -> System.getProperty("java.runtime.version"))
    contentType = "text/html"
    mustache("/index", "runtime" -> runtime)
  }

  get("/land-practice-chart") {
    val paramMap = landParamMap()

    val data = multiParams.keys
        .filter(k => paramMap.contains(k))
        .map({ k =>
          val v1 = paramMap.get(k).get
          val v2 = multiParams.get(k) match {
            case Some(Seq(x, _*)) => x.toDouble
          }
          (v1, v2)
        }).toMap

    /*
    val data = ListMap( // Important that iteration happens in order
        (Reading.Nutrients, Group.Previous, Rating.A) -> 21.0,
        (Reading.Nutrients, Group.Previous, Rating.B) -> 19.0,
        (Reading.Nutrients, Group.Previous, Rating.C) -> 37.0,
        (Reading.Nutrients, Group.Previous, Rating.D) -> 23.0,
        (Reading.Nutrients, Group.Current, Rating.A) -> 21.0,
        (Reading.Nutrients, Group.Current, Rating.B) -> 28.0,
        (Reading.Nutrients, Group.Current, Rating.C) -> 32.0,
        (Reading.Nutrients, Group.Current, Rating.D) -> 19.0,
        (Reading.Herbicides, Group.Previous, Rating.A) -> 28.0,
        (Reading.Herbicides, Group.Previous, Rating.B) -> 50.0,
        (Reading.Herbicides, Group.Previous, Rating.C) -> 16.0,
        (Reading.Herbicides, Group.Previous, Rating.D) ->  6.0,
        (Reading.Herbicides, Group.Current, Rating.A) -> 23.0,
        (Reading.Herbicides, Group.Current, Rating.B) -> 53.0,
        (Reading.Herbicides, Group.Current, Rating.C) -> 20.0,
        (Reading.Herbicides, Group.Current, Rating.D) ->  4.0,
        (Reading.Soil, Group.Previous, Rating.A) -> 30.0,
        (Reading.Soil, Group.Previous, Rating.B) -> 40.0,
        (Reading.Soil, Group.Previous, Rating.C) -> 19.0,
        (Reading.Soil, Group.Previous, Rating.D) -> 11.0,
        (Reading.Soil, Group.Current, Rating.A) -> 27.0,
        (Reading.Soil, Group.Current, Rating.B) -> 35.0,
        (Reading.Soil, Group.Current, Rating.C) -> 29.0,
        (Reading.Soil, Group.Current, Rating.D) -> 09.0)*/

    val title = try {
      params("title")
    } catch {
      case _: NoSuchElementException => "Land Practices Chart"
    }

    val chart = createLandPracticeChart(
        new Dimension(500, 500),
        data, title)

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

  private def landParamMap() = {
    import ChartData._
    val mappings = (
      Seq(
        'N' -> Reading.Nutrients,
        'H' -> Reading.Herbicides,
        'S' -> Reading.Soil),
      Seq(
        'p' -> Group.Previous,
        'c' -> Group.Current),
      Rating.values.map(r => (s"$r".charAt(0), r))
    )
    // Should build an param => key sequence. eg.
    //   Seq(("NpA", (Reading.Nutrients, Group.Previous, Rating.A)), ...)
    val ptups = for (re <- mappings._1; g <- mappings._2; r8 <- mappings._3)
      yield (""+re._1+g._1+r8._1, (re._2, g._2, r8._2))
    // Output as map
    ptups.toMap
  }

  private def createLandPracticeChart(
       dimension: Dimension,
       data: Map[ChartData.LandPracticeDataKey, Double],
       title: String) = {
    val renderer = new au.edu.uq.aorra.charts.BarLegendRenderer
    val chart = new LandPracticeChart(renderer)
    val result = chart.createChart(data);
    result.addSubtitle(new TextTitle(title, TITLE_FONT))
    val wrapper = new DimensionsWrapper(result, dimension)
    wrapper
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
