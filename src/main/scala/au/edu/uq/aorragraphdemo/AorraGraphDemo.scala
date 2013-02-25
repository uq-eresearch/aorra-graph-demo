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
  ChartData, ChartRenderer, GrazingPracticeChart, LandPracticeChart, SvgWrapper}
import ereefs.charts.{
  CategoryDatasetBuilder, ChartContentWrapper,
  DimensionsWrapper, ProgressChart, ProgressChartBuilder}
import ereefs.images._
import java.util.NoSuchElementException
import ereefs.charts.BeerCoaster
import org.jfree.data.category.DefaultCategoryDataset
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.geom.Rectangle2D
import java.awt.RenderingHints
import org.apache.batik.dom.svg.SVGDOMImplementation
import org.apache.batik.dom.svg.SVGDOMImplementation
import org.w3c.dom.svg.SVGDocument
import org.apache.batik.svggen.SVGGraphics2D
import java.io.CharArrayWriter
import scala.collection.JavaConversions._
import org.apache.batik.svggen.DefaultImageHandler
import org.apache.batik.svggen.DefaultExtensionHandler

class AorraGraphDemo extends ScalatraFilter with ScalateSupport {

  get("/") {
    val runtime = Map(
        "name" -> System.getProperty("java.runtime.name"),
        "version" -> System.getProperty("java.runtime.version"))
    contentType = "text/html"
    mustache("/index", "runtime" -> runtime)
  }

  get("/grazing-practice-chart.:format") {
    val f = grazingPracticeChart.toFormat(params("format"))
    f.mimetype foreach { contentType = _ }
    f.output getOrElse halt(400, "Unsupported image format requested.")
  }

  get("/indicator-chart.:format") {
    val f = indicatorChart.toFormat(params("format"))
    f.mimetype foreach { contentType = _ }
    f.output getOrElse halt(400, "Unsupported image format requested.")
  }

  get("/land-practice-chart.:format") {
    val f = landPracticeChart.toFormat(params("format"))
    f.mimetype foreach { contentType = _ }
    f.output getOrElse halt(400, "Unsupported image format requested.")
  }

  get("/progress-chart.:format") {
    val f = progressChart.toFormat(params("format"))
    f.mimetype foreach { contentType = _ }
    f.output getOrElse halt(400, "Unsupported image format requested.")
  }

  private def grazingPracticeChart = {
    import ChartData._

    val data = mapParamsToData(grazingParamMap)

    val chart = (new GrazingPracticeChart(
        (paramOrBlank("pLabel"),paramOrBlank("cLabel")))).createChart(
            paramOrBlank("title"), data)
    val dimension = new Dimension(500, 500)
    val wrapper = new DimensionsWrapper(chart, dimension)

    val svg = (new ChartRenderer(wrapper)).render()
    SvgWrapper(svg, params.getAs[Int]("width"), params.getAs[Int]("height"))
  }

  private def indicatorChart() = {
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

    val svg = (new ChartRenderer(chart)).render()
    SvgWrapper(svg, params.getAs[Int]("width"), params.getAs[Int]("height"))
  }

  private def landPracticeChart() = {
    val chart = (new LandPracticeChart(
        (paramOrBlank("pLabel"),paramOrBlank("cLabel")))).createChart(
                paramOrBlank("title"), mapParamsToData(landParamMap))
    val wrapper = new DimensionsWrapper(chart, new Dimension(500, 500))

    val svg = (new ChartRenderer(wrapper)).render()
    SvgWrapper(svg, params.getAs[Int]("width"), params.getAs[Int]("height"))
  }


  private def progressChart() = {
    val builder = new ProgressChartBuilder(
        paramOrBlank("tl"),
        paramOrBlank("tr"),
        multiParams("max").headOption map { _.toInt} getOrElse 100)
    val chart = builder.get(multiParams("value").headOption match {
      case Some(v) => v.toFloat
      case None => halt(400, """Parameter "value" is required.""")
    })

    val svg = renderSvgProgressChart(chart)
    SvgWrapper(svg, params.getAs[Int]("width"), params.getAs[Int]("height"))
  }

  private def renderSvgProgressChart(
      chart: ProgressChart) = {
    // Get a DOMImplementation.
    val impl = SVGDOMImplementation.getDOMImplementation()
    val svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
    val doc = impl.createDocument(svgNS, "svg", null).asInstanceOf[SVGDocument]

    // Create an instance of the SVG Generator.
    val g2 = new SVGGraphics2D(doc,
        new DefaultImageHandler(),
        new DefaultExtensionHandler(),
        true
    )

    val d = chart.getMinDimension
    g2.setRenderingHint(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON)
    chart.draw(g2, new Rectangle2D.Double(0, 0, d.width, d.height))
    g2.setSVGCanvasSize(d)

    val cw = new CharArrayWriter()
    g2.stream(cw, true)
    g2.dispose

    cw.toString()
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
