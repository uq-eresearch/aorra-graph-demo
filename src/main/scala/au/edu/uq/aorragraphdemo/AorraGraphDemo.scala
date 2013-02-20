package au.edu.uq.aorragraphdemo

import scala.collection.immutable.ListMap
import org.scalatra._
import scalate.ScalateSupport
import java.awt.Dimension
import java.io.ByteArrayOutputStream
import org.jfree.data.category.CategoryDataset
import org.jfree.chart.title.TextTitle
import ereefs.content.Content
import au.edu.uq.aorra.charts.{
  ChartData, GrazingPracticeChart, LandPracticeChart}
import ereefs.charts.{
  CategoryDatasetBuilder, ChartContentWrapper, ChartRenderer,
  DimensionsWrapper, ProgressChart, ProgressChartBuilder}
import ereefs.images._
import java.util.NoSuchElementException
import ereefs.charts.BeerCoaster
import org.jfree.data.category.DefaultCategoryDataset
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.geom.Rectangle2D
import java.awt.RenderingHints

class AorraGraphDemo extends ScalatraFilter with ScalateSupport {

  get("/") {
    val runtime = Map(
        "name" -> System.getProperty("java.runtime.name"),
        "version" -> System.getProperty("java.runtime.version"))
    contentType = "text/html"
    mustache("/index", "runtime" -> runtime)
  }

  get("/grazing-practice-chart") {
    import ChartData._

    val data = mapParamsToData(grazingParamMap)

    val chart = (new GrazingPracticeChart(
        (paramOrBlank("pLabel"),paramOrBlank("cLabel")))).createChart(
            paramOrBlank("title"), data)
    val dimension = new Dimension(500, 500)
    val wrapper = new DimensionsWrapper(chart, dimension)

    val content = new ChartContentWrapper(wrapper)
    contentType = content.getContentType
    transformToBytes(content)
  }

  get("/indicator-chart") {
    val chart = new BeerCoaster()
    BeerCoaster.Category.values.foreach({ category =>
      conditionOption(category.toString) map {
        chart.setCondition(category, _)
      }
    })
    BeerCoaster.Indicator.values.foreach({ indicator =>
      conditionOption(indicator.toString) map {
        chart.setCondition(indicator, _)
      }
    })
    conditionOption("overall") map { chart.setOverallCondition(_) }

    val content = new ChartContentWrapper(chart)
    contentType = content.getContentType
    transformToBytes(content)
  }

  get("/land-practice-chart") {
    val chart = (new LandPracticeChart(
        (paramOrBlank("pLabel"),paramOrBlank("cLabel")))).createChart(
                paramOrBlank("title"), mapParamsToData(landParamMap))
    val wrapper = new DimensionsWrapper(chart, new Dimension(500, 500))

    val content = new ChartContentWrapper(wrapper)
    contentType = content.getContentType
    transformToBytes(content)
  }

  get("/progress-chart") {
    val builder = new ProgressChartBuilder(
        paramOrBlank("tl"),
        paramOrBlank("tr"),
        multiParams("max").headOption map { _.toInt} getOrElse 100)
    val chart = builder.get(multiParams("value").headOption match {
      case Some(v) => v.toFloat
      case None => halt(400, """Parameter "value" is required.""")
    })

    val transform = scalingTransform(chart.getMinDimension, requestedDimensions)

    val content = chartImageContent(renderProgressChart(chart, transform))
    contentType = content.getContentType
    transformToBytes(content)
  }

  private def renderProgressChart(
      chart: ProgressChart,
      transform: AffineTransform) = {
    val d = chart.getMinDimension
    val img0 = new BufferedImage(
        (d.width * transform.getScaleX()).ceil.toInt,
        (d.height * transform.getScaleY()).ceil.toInt,
        BufferedImage.TYPE_INT_ARGB)
    val g2 = img0.createGraphics()
    g2.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON)
    g2.setTransform(transform)
    chart.draw(g2, new Rectangle2D.Double(0, 0, d.width, d.height))
    g2.dispose
    img0
  }

  private def scalingTransform(
      current: Dimension,
      requested: (Option[Int], Option[Int])) = {
    def ratio(d: (Option[Int], Option[Int])): Double = d match {
      case (None, None) => 1 // Identity
      case (Some(x), None) =>
        x.toDouble / current.getWidth
      case (None, Some(y)) =>
        y.toDouble / current.getHeight
      case (Some(x), Some(y)) => // Pick the lesser
        Math.min(ratio (d._1, None), ratio (None, d._2))
    }
    val r = ratio(requested)
    AffineTransform.getScaleInstance(r, r)
  }

  private def requestedDimensions(): (Option[Int], Option[Int]) = {
    def paramOption(k: String) = {
      try {
        multiParams(k).headOption map { _.toInt }
      } catch {
        case e: NumberFormatException => None
      }
    }
    (paramOption("width"), paramOption("height"))
  }

  private def paramOrBlank(k: String) = try {
    params(k)
  } catch {
    case _: NoSuchElementException => ""
  }

  private def conditionOption(name: String) = {
    multiParams(name.toLowerCase) match {
      case Seq() => None
      case Seq(c, _*) => try {
        Some(BeerCoaster.Condition.valueOf(c))
      } catch {
        case _: IllegalArgumentException => None
      }
    }
  }

  private def mapParamsToData[A](paramMap: Map[String, A]): Map[A, Double] = {
    multiParams.keys
      .filter(k => paramMap.contains(k))
      .map({ k =>
        val v1 = paramMap.get(k).get
        val v2 = multiParams.get(k) match {
          case Some(Seq(x, _*)) => x.toDouble
        }
        (v1, v2)
      }).toMap
  }

  private def grazingParamMap() = {
    import ChartData._
    val mappings = (
      Seq(
        'p' -> Group.Previous,
        'c' -> Group.Current),
      Rating.values.map(r => (s"$r".charAt(0), r))
    )
    // Should build an param => key sequence. eg.
    //   Seq(("pA", (Group.Previous, Rating.A)), ...)
    val ptups = for (g <- mappings._1; r8 <- mappings._2)
      yield (""+g._1+r8._1, (g._2, r8._2))
    // Output as map
    ptups.toMap
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

  private def chartImageContent(image: BufferedImage) = {
    new ImageContent(new ImageRenderer {
      def render = image
    })
  }

  private def transformToBytes(content: Content) = {
      val byteStream = new ByteArrayOutputStream()
    content.write(byteStream)
    byteStream.toByteArray
  }


}
