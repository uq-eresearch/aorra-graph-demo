package au.edu.uq.aorra.charts

import ereefs.charts.Configuration.AXIS_LABEL_COLOR
import ereefs.charts.Configuration.AXIS_LABEL_FONT
import ereefs.charts.Configuration.CAXIS_LABEL_FONT
import ereefs.charts.Configuration.COLOR_A
import ereefs.charts.Configuration.COLOR_A_TRANS
import ereefs.charts.Configuration.COLOR_B
import ereefs.charts.Configuration.COLOR_B_TRANS
import ereefs.charts.Configuration.COLOR_C
import ereefs.charts.Configuration.COLOR_C_TRANS
import ereefs.charts.Configuration.COLOR_D
import ereefs.charts.Configuration.COLOR_D_TRANS
import ereefs.charts.Configuration.TITLE_FONT
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Paint
import java.awt.Shape
import java.awt.TexturePaint
import java.awt.font.FontRenderContext
import java.awt.font.GlyphVector
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import org.apache.commons.lang3.tuple.Pair
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.LegendItem
import org.jfree.chart.LegendItemCollection
import org.jfree.chart.LegendItemSource
import org.jfree.chart.axis.CategoryAxis
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.axis.NumberTickUnit
import org.jfree.chart.axis.ValueAxis
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.category.BarRenderer
import org.jfree.chart.renderer.category.CategoryItemRendererState
import org.jfree.chart.renderer.category.StandardBarPainter
import org.jfree.chart.title.LegendTitle
import org.jfree.chart.title.TextTitle
import org.jfree.data.Range
import org.jfree.data.category.CategoryDataset
import org.jfree.ui.RectangleEdge
import org.jfree.ui.RectangleInsets
import ereefs.charts.GraphUtils
import org.jfree.data.category.DefaultCategoryDataset

class GrazingPracticeChart(val periodLabels: (String,String)) {

  type DataKey = ChartData.GrazingPracticeDataKey

  private class CustomBarRenderer extends BarRenderer {

    private val BAR_COLORS = Map(
      (0, 0) -> COLOR_A_TRANS,
      (1, 0) -> COLOR_A,
      (0, 1) -> COLOR_B_TRANS,
      (1, 1) -> COLOR_B,
      (0, 2) -> COLOR_C_TRANS,
      (1, 2) -> COLOR_C,
      (0, 3) -> COLOR_D_TRANS,
      (1, 3) -> COLOR_D)

    override def drawItem(
      g2: Graphics2D,
      state: CategoryItemRendererState,
      dataArea: Rectangle2D,
      plot: CategoryPlot,
      domainAxis: CategoryAxis,
      rangeAxis: ValueAxis,
      dataset: CategoryDataset,
      row: Int, column: Int, pass: Int) = {
      super.drawItem(g2, state, dataArea, plot, domainAxis,
        rangeAxis, dataset, row, column, pass)
      if ((pass == 0) && (row == 1) && (column == 3)) {
        // Workaround: because the dataArea sits on the the Axis the 0% gridline gets drawn
        // over the category axis making it gray. To fix this as we draw another black line
        // to restore the black axis.
        g2.setColor(Color.black)
        g2.setStroke(new BasicStroke(2))
        g2.drawLine(
          dataArea.getMinX.toInt,
          dataArea.getMaxY.toInt,
          dataArea.getMaxX.toInt,
          dataArea.getMaxY.toInt)
        g2.drawLine(
          dataArea.getMinX.toInt,
          dataArea.getMinY.toInt,
          dataArea.getMinX.toInt,
          dataArea.getMaxY.toInt)
      }
    }

    override def getItemPaint(row: Int, column: Int) = {
      val color = BAR_COLORS.get((row, column)).get
      if (row == 0) {
        val striped = GraphUtils.createStripedTexture(color)
        val anchor = new Rectangle2D.Double(0, 0, striped.getWidth(), striped.getHeight())
        new TexturePaint(striped, anchor)
      } else {
        color
      }
    }
  }

  private def createLegend = {
    val legendItems = new LegendItemCollection()
    val frc = new FontRenderContext(null, true, true)
    val legenfont = new Font(Font.SANS_SERIF, Font.BOLD, 12)
    val gv = legenfont.createGlyphVector(frc, Array('X', 'X'))
    val shape = gv.getVisualBounds();
    {
      val striped = GraphUtils.createStripedTexture(Color.black)
      val anchor = new Rectangle2D.Double(0, 0, striped.getWidth(), striped.getHeight())
      val li = new LegendItem(periodLabels._1, null, null, null,
        shape, new TexturePaint(striped, anchor))
      li.setLabelFont(legenfont)
      legendItems.add(li)
    }
    {
      val li = new LegendItem(periodLabels._2, null, null, null,
        shape, Color.black)
      li.setLabelFont(legenfont)
      legendItems.add(li)
    }
    val legend = new LegendTitle(new LegendItemSource() {
      override def getLegendItems = legendItems
    })
    legend.setPosition(RectangleEdge.BOTTOM)
    legend.setMargin(new RectangleInsets(0, 30, 0, 0))
    legend.setPadding(RectangleInsets.ZERO_INSETS)
    legend.setLegendItemGraphicPadding(new RectangleInsets(0, 20, 0, 0))
    legend
  }

  def createChart(titleText: String, data: Map[DataKey, Double]) = {
    val chart = ChartFactory.createBarChart(
      "", // chart title
      "", // domain axis label
      "", // range axis label
      getDataset(data), // data
      PlotOrientation.VERTICAL, // the plot orientation
      false, // legend
      true, // tooltips
      false // urls
      )
    val title = new TextTitle(titleText, TITLE_FONT)
    title.setPadding(new RectangleInsets(10, 0, 0, 0))
    chart.setTitle(title)
    chart.addLegend(createLegend)
    val plot = chart.getPlot().asInstanceOf[CategoryPlot]
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
    rangeAxis.setLabel("% of graziers")
    rangeAxis.setAxisLineStroke(new BasicStroke(2))
    rangeAxis.setAxisLinePaint(Color.black)
    rangeAxis.setTickMarksVisible(false)
    rangeAxis.setLabelPaint(AXIS_LABEL_COLOR)
    rangeAxis.setLabelFont(AXIS_LABEL_FONT)
    rangeAxis.setLabelInsets(new RectangleInsets(0, 0, 0, -10))
    rangeAxis.setUpperMargin(0)
    rangeAxis.setAutoRange(false)
    rangeAxis.setRange(new Range(0, 100))

    val cAxis = plot.getDomainAxis()
    cAxis.setTickMarksVisible(false)
    cAxis.setAxisLinePaint(Color.black)
    cAxis.setAxisLineStroke(new BasicStroke(2))
    cAxis.setLabel("")
    cAxis.setTickLabelsVisible(true)
    cAxis.setUpperMargin(0.05)
    cAxis.setLowerMargin(0.05)
    cAxis.setTickLabelFont(CAXIS_LABEL_FONT)
    cAxis.setTickLabelPaint(Color.black)
    val renderer = new CustomBarRenderer()
    plot.setRenderer(renderer)
    renderer.setDrawBarOutline(false)
    renderer.setBaseItemLabelsVisible(false)
    renderer.setShadowVisible(false)
    renderer.setBarPainter(new StandardBarPainter())
    renderer.setMaximumBarWidth(0.08)
    renderer.setItemMargin(0.01)
    chart
  }

  private def getDataset(data: Map[DataKey, Double]) = {
    val dataset = new DefaultCategoryDataset()
    data.toSeq.sortBy({ _._1._2 }).foreach({ p =>
      val ((group, rating), value) = p
      dataset.addValue(value, group.toString, rating.toString)
    })
    dataset
  }

}

