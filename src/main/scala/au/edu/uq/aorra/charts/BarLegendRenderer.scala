package au.edu.uq.aorra.charts

import scala.collection.immutable.HashMap

import java.awt.{BasicStroke, Color, Graphics2D}
import java.awt.geom.{AffineTransform, Rectangle2D}

import org.jfree.chart.renderer.category.GroupedStackedBarRenderer
import org.jfree.chart.renderer.category.CategoryItemRendererState
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.axis.{CategoryAxis,ValueAxis}
import org.jfree.data.category.CategoryDataset

import ereefs.charts.GraphUtils
import ereefs.charts.Configuration.LEGEND_FONT

class BarLegendRenderer extends GroupedStackedBarRenderer {

  private var legend = HashMap[(Int, Int),String](
    (3, 0) -> "Nutrients 2008-2009",
    (7, 0) -> "Nutrients 2009-2010",
    (3, 1) -> "Herbicides 2008-2009",
    (7, 1) -> "Herbicides 2009-2010",
    (3, 2) -> "Soil 2008-2009",
    (7, 2) -> "Soil 2009-2010"
  )

  override def drawItem(
      g2: Graphics2D,
      state: CategoryItemRendererState,
      dataArea: Rectangle2D,
      plot: CategoryPlot,
      domainAxis: CategoryAxis,
      rangeAxis: ValueAxis,
      dataset: CategoryDataset,
      row: Int, column: Int, pass: Int) = {
    super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset,
                    row, column, pass)
    if (pass == 2) {
      afterDrawItem(g2, state, dataArea, plot, domainAxis, row, column)
      if (row == 7) redrawBlackLines(g2, dataArea)
    }
  }

  def afterDrawItem(
      g2: Graphics2D,
      state: CategoryItemRendererState,
      dataArea: Rectangle2D,
      plot: CategoryPlot,
      domainAxis: CategoryAxis,
      row: Int, column: Int) {
    // after the stacked bar is completely rendered draw the glow text into it.
    val label = legend.get((row, column)) getOrElse(return)
    val barW0 = calculateBarW0(plot, plot.getOrientation(), dataArea,
        domainAxis, state, row, column)
    val labelx = barW0 + state.getBarWidth()/2
    // TODO: does not seem to be correct, but ok for now
    val labely = dataArea.getMinY()+dataArea.getHeight()*.10
    val angle = GraphUtils.toRadians(-90)
    g2.setFont(LEGEND_FONT)
    g2.setColor(Color.WHITE)
    val g = new GraphUtils(g2)
    val img = g.drawGlowString(
            label, LEGEND_FONT, Color.black, Color.white, 6)
    val saveT = g2.getTransform()
    val transform = new AffineTransform()
    // jfree chart seem to be using the transform on the Graphics2D object
    // for scaling when the window gets very small or large
    // therefore we can not just overwrite the transform but have to factor it into
    // our rotation and translation transformations.
    transform.concatenate(saveT)
    transform.concatenate(AffineTransform.getRotateInstance(angle, labelx, labely))
    // first translate to the center right
    transform.concatenate(AffineTransform.getTranslateInstance(
        -img.getWidth(), -img.getHeight()/2))
    g2.setTransform(transform)
    g2.drawImage(img, null, labelx.toInt, labely.toInt)
    g2.setTransform(saveT)
  }

  /**
   * Workaround: because the dataArea sits on the the Axis the 0% gridline
   * gets drawn over the category axis making it gray. To fix this as we
   * draw another black line to restore the black axis.
   */
  def redrawBlackLines(g2: Graphics2D, da: Rectangle2D) {
    g2.setColor(Color.black)
    g2.setStroke(new BasicStroke(2))
    val Seq(x0,x1,y0,y1) =
      Seq(da.getMinX, da.getMaxX, da.getMinY, da.getMaxY) map { _.toInt }
    g2.drawLine(x0, y1, x1, y1)
    g2.drawLine(x0, y0, x0, y1)
  }



}