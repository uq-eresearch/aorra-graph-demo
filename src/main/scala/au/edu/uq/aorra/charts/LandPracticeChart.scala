package au.edu.uq.aorra.charts

import ereefs.charts.Configuration.AXIS_LABEL_COLOR
import ereefs.charts.Configuration.AXIS_LABEL_FONT
import ereefs.charts.Configuration.COLOR_A
import ereefs.charts.Configuration.COLOR_A_TRANS
import ereefs.charts.Configuration.COLOR_B
import ereefs.charts.Configuration.COLOR_B_TRANS
import ereefs.charts.Configuration.COLOR_C
import ereefs.charts.Configuration.COLOR_C_TRANS
import ereefs.charts.Configuration.COLOR_D
import ereefs.charts.Configuration.COLOR_D_TRANS
import ereefs.charts.Configuration.LEGEND_FONT
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Shape
import java.awt.font.FontRenderContext
import java.awt.font.GlyphVector
import org.apache.commons.lang3.tuple.Pair
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.LegendItem
import org.jfree.chart.LegendItemCollection
import org.jfree.chart.LegendItemSource
import org.jfree.chart.axis.CategoryAxis
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.axis.NumberTickUnit
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer
import org.jfree.chart.renderer.category.StandardBarPainter
import org.jfree.chart.title.LegendTitle
import org.jfree.data.KeyToGroupMap
import org.jfree.data.category.CategoryDataset
import org.jfree.ui.RectangleEdge
import org.jfree.ui.RectangleInsets
import com.google.common.collect.ImmutableList
import ChartData._
import org.jfree.data.category.DefaultCategoryDataset

class LandPracticeChart(val renderer: GroupedStackedBarRenderer) {

  type DataKey = ChartData.LandPracticeDataKey

  def createLegend() = {
    val legendItems = new LegendItemCollection
    val frc = new FontRenderContext(null, true, true)
    val gv = LEGEND_FONT.createGlyphVector(frc, Array('X'))
    val shape = gv.getGlyphVisualBounds(0)
    Rating.values map { r =>
      new LegendItem(r.toString, null, null, null, shape, color(r))
    } foreach { li =>
      li.setLabelFont(LEGEND_FONT)
      legendItems.add(li)
    }
    val legend = new LegendTitle(new LegendItemSource() {
      override def getLegendItems = legendItems
    })
    legend.setPosition(RectangleEdge.BOTTOM)
    legend
  }

  def createChart(data: Map[DataKey, Double]) = {
    val dataset = createDataset(data)
    val chart = ChartFactory.createStackedBarChart(
      "", // chart title
      "", // domain axis label
      "", // range axis label
      dataset, // data
      PlotOrientation.VERTICAL, // the plot orientation
      false, // legend
      true, // tooltips
      false // urls
      )
    chart.addLegend(createLegend)
    val plot = chart.getPlot.asInstanceOf[CategoryPlot]
    plot.setBackgroundPaint(Color.white)
    plot.setOutlineVisible(false)
    plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0))
    plot.setDomainGridlinesVisible(false)
    plot.setRangeGridlinesVisible(true)
    plot.setRangeGridlinePaint(Color.gray)
    plot.setRangeGridlineStroke(new BasicStroke(2))

    val rangeAxis = plot.getRangeAxis.asInstanceOf[NumberAxis]
    rangeAxis.setAutoTickUnitSelection(true)
    rangeAxis.setTickUnit(new NumberTickUnit(20))
    rangeAxis.setAxisLineVisible(true)
    rangeAxis.setLabel("% of landholders")
    rangeAxis.setAxisLineStroke(new BasicStroke(2))
    rangeAxis.setAxisLinePaint(Color.black)
    rangeAxis.setTickMarksVisible(false)
    rangeAxis.setLabelPaint(AXIS_LABEL_COLOR)
    rangeAxis.setLabelFont(AXIS_LABEL_FONT)
    rangeAxis.setLabelInsets(new RectangleInsets(0, 0, 0, -10))
    rangeAxis.setUpperMargin(0)

    val cAxis = plot.getDomainAxis()
    cAxis.setTickMarksVisible(false)
    cAxis.setAxisLinePaint(Color.black)
    cAxis.setAxisLineStroke(new BasicStroke(2))
    cAxis.setLabel("")
    cAxis.setTickLabelsVisible(false)
    cAxis.setCategoryMargin(0.05)
    cAxis.setUpperMargin(0.1)
    cAxis.setLowerMargin(0)
    val renderer = this.renderer match {
      case null => new GroupedStackedBarRenderer()
      case r: GroupedStackedBarRenderer => r
    }
    plot.setRenderer(renderer)

    seriesColors.zipWithIndex foreach { v =>
      renderer.setSeriesPaint(v._2, v._1)
    }

    renderer.setRenderAsPercentages(true)
    renderer.setDrawBarOutline(false)
    renderer.setBaseItemLabelsVisible(false)
    renderer.setShadowVisible(false)
    renderer.setBarPainter(new StandardBarPainter())
    renderer.setItemMargin(0.10)
    val map = new KeyToGroupMap()
    for (g <- Group.values; r8 <- Rating.values) {
      map.mapKeyToGroup(s"${g}_$r8", g.toString)
    }
    renderer.setSeriesToGroupMap(map)
    chart
  }

  private def createDataset(data: Map[DataKey, Double]) = {
    val dataset = new DefaultCategoryDataset
    for (re <- Reading.values; g <- Group.values; r8 <- Rating.values) {
      data.get((re, g, r8)) match {
        case Some(v) =>
          dataset.addValue(v, s"${g}_$r8", re.toString)
        case None => // Do nothing
      }
    }
    dataset
  }

  private def color(r: Rating.Value) = r match {
    case Rating.A => COLOR_A
    case Rating.B => COLOR_B
    case Rating.C => COLOR_C
    case Rating.D => COLOR_D
  }

  private def transparentColor(r: Rating.Value) = r match {
    case Rating.A => COLOR_A_TRANS
    case Rating.B => COLOR_B_TRANS
    case Rating.C => COLOR_C_TRANS
    case Rating.D => COLOR_D_TRANS
  }

  private def seriesColors = {
    val rSeq = Rating.values.toSeq
    rSeq.map { transparentColor(_) } ++ rSeq.map { color(_) }
  }

}