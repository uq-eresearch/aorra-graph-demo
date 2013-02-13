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
    <html>
      <head>
        <title>AORRA Graph Demo</title>
        <script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
        <style type="text/css">
        <![CDATA[
        form label {
            display: inline-block;
            width: 6em;
        }
        ]]>
        </style>
      </head>
      <body>
        <h1>Chart Examples</h1>
        <form id="progress-chart">
          <fieldset>
            <legend>Progress Chart</legend>
            <label for="tl">Top Left Text</label>
            <input type="text" name="tl" value="Horticulture"/><br/>
            <label for="tr">Top Right Text</label>
            <input type="text" name="tr" value="2013 targets"/><br/>
            <label for="value">Value</label>
            <input type="text" name="value" value="42"/><br/>
            <label for="max">Max</label>
            <input type="text" name="max" value="100"/><br/>
            <label for="width">Dimensions</label>
            <input type="text" name="width" size="3"/> &times;
            <input type="text" name="height" size="3"/><br/>
            <button type="submit">Render</button>
            <hr />
            <img/>
          </fieldset>
        </form>
        <fieldset>
          <legend>Sugar Practice Chart</legend>
          <img src="sugarcane-practice-chart"/>
        </fieldset>
        <script type="text/javascript">
        <![CDATA[
        $('#progress-chart').submit(function() {
          var serialize = $(this).serialize();
          $(this).find('img').attr('src', 'progress-chart?'+serialize);
          return false;
        });
        $('#progress-chart').submit();
        ]]>
        </script>
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
