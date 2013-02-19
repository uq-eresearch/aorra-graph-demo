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
import ereefs.charts.BeerCoaster

class AorraGraphDemo extends ScalatraFilter with ScalateSupport {

  get("/") {
    val runtime = Map(
        "name" -> System.getProperty("java.runtime.name"),
        "version" -> System.getProperty("java.runtime.version"))
    contentType = "text/html"
    mustache("/index", "runtime" -> runtime)
  }

  get("/indicator-chart") {
    val categories = BeerCoaster.Category.values map { _.toString.toLowerCase }
    val indicators = BeerCoaster.Indicator.values map { _.toString.toLowerCase }

    val chart = new BeerCoaster()

    categories.foreach({ x =>
      multiParams(x) match {
        case Seq() => // Do nothing
        case Seq(c, _*) => try {
          val category = BeerCoaster.Category.valueOf(x.toUpperCase)
          val condition = BeerCoaster.Condition.valueOf(c)
          chart.setCondition(category, condition)
        } catch {
          case _: IllegalArgumentException => // Skip
        }
      }
    })
    indicators.foreach({ x =>
      multiParams(x) match {
        case Seq() => // Do nothing
        case Seq(c, _*) => try {
          val indicator = BeerCoaster.Indicator.valueOf(x.toUpperCase)
          val condition = BeerCoaster.Condition.valueOf(c)
          chart.setCondition(indicator, condition)
        } catch {
          case _: IllegalArgumentException => // Skip
        }
      }
    })
    multiParams("overall") match {
      case Seq() => // Do nothing
      case Seq(c, _*) => try {
        val condition = BeerCoaster.Condition.valueOf(c)
        chart.setOverallCondition(condition)
      } catch {
        case _: IllegalArgumentException => // Skip
      }
    }

    val content = new ChartContentWrapper(chart)
    contentType = content.getContentType
    transformToBytes(content)
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


    def paramOrBlank(k: String) = try {
      params(k)
    } catch {
      case _: NoSuchElementException => ""
    }

    val chart = createLandPracticeChart(
        new Dimension(500, 500),
        data,
        paramOrBlank("title"),
        (paramOrBlank("pLabel"),paramOrBlank("cLabel")))

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
       title: String,
       periodLabels: (String, String)) = {
    val renderer = new au.edu.uq.aorra.charts.BarLegendRenderer(periodLabels)
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
