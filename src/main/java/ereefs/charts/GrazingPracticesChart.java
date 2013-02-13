package ereefs.charts;

import static ereefs.charts.Configuration.AXIS_LABEL_COLOR;
import static ereefs.charts.Configuration.AXIS_LABEL_FONT;
import static ereefs.charts.Configuration.CAXIS_LABEL_FONT;
import static ereefs.charts.Configuration.COLOR_A;
import static ereefs.charts.Configuration.COLOR_A_TRANS;
import static ereefs.charts.Configuration.COLOR_B;
import static ereefs.charts.Configuration.COLOR_B_TRANS;
import static ereefs.charts.Configuration.COLOR_C;
import static ereefs.charts.Configuration.COLOR_C_TRANS;
import static ereefs.charts.Configuration.COLOR_D;
import static ereefs.charts.Configuration.COLOR_D_TRANS;
import static ereefs.charts.Configuration.TITLE_FONT;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import com.google.common.collect.ImmutableMap;

public class GrazingPracticesChart {

    private static class CustomBarRenderer extends BarRenderer {

        private static Map<Pair<Integer, Integer>, Color> BAR_COLORS =
                new ImmutableMap.Builder<Pair<Integer, Integer>, Color>()
                    .put(Pair.of(0, 0), COLOR_A_TRANS)
                    .put(Pair.of(1, 0), COLOR_A)
                    .put(Pair.of(0, 1), COLOR_B_TRANS)
                    .put(Pair.of(1, 1), COLOR_B)
                    .put(Pair.of(0, 2), COLOR_C_TRANS)
                    .put(Pair.of(1, 2), COLOR_C)
                    .put(Pair.of(0, 3), COLOR_D_TRANS)
                    .put(Pair.of(1, 3), COLOR_D)
                    .build();

        @Override
        public void drawItem(Graphics2D g2, CategoryItemRendererState state,
                Rectangle2D dataArea, CategoryPlot plot,
                CategoryAxis domainAxis, ValueAxis rangeAxis,
                CategoryDataset dataset, int row, int column, int pass) {
            super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row,
                    column, pass);
//            System.out.println(String.format("row %s, column %s, pass %s", row, column, pass));
            if((pass == 0) && (row == 1)&& (column == 3)) {
                // Workaround: because the dataArea sits on the the Axis the 0% gridline gets drawn 
                // over the category axis making it gray. To fix this as we draw another black line
                // to restore the black axis.
                g2.setColor(Color.black);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine((int)dataArea.getMinX(), (int)dataArea.getMaxY(), (int)dataArea.getMaxX(), (int)dataArea.getMaxY());
                g2.drawLine((int)dataArea.getMinX(), (int)dataArea.getMinY(), (int)dataArea.getMinX(), (int)dataArea.getMaxY());
            }
        }
        
        @Override
        public Paint getItemPaint(int row, int column) {
            Color color = BAR_COLORS.get(Pair.of(row, column));
            if(row == 0) {
                BufferedImage striped = GraphUtils.createStripedTexture(color);
                Rectangle2D anchor = new Rectangle2D.Double(0, 0, striped.getWidth(), striped.getHeight());
                return new TexturePaint(striped, anchor);
            } else {
                return color;
            }
        }
    }

    private static LegendTitle createLegend() {
        final LegendItemCollection legendItems = new LegendItemCollection();
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Font legenfont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
        GlyphVector gv = legenfont.createGlyphVector(frc, new char[] {'X', 'X'});
        Shape shape = gv.getVisualBounds();
        {
            BufferedImage striped = GraphUtils.createStripedTexture(Color.black);
            Rectangle2D anchor = new Rectangle2D.Double(0, 0, striped.getWidth(), striped.getHeight());
            LegendItem li = new LegendItem("2008-2009", null, null, null,
                    shape, new TexturePaint(striped, anchor));
            li.setLabelFont(legenfont);
            legendItems.add(li);
        }
        {
            LegendItem li = new LegendItem("2009-2010", null, null, null, shape, Color.black);
            li.setLabelFont(legenfont);
            legendItems.add(li);
        }
        LegendTitle legend = new LegendTitle(new LegendItemSource() {
            @Override
            public LegendItemCollection getLegendItems() {
                return legendItems;
            }});
        legend.setPosition(RectangleEdge.BOTTOM);
        legend.setMargin(new RectangleInsets(0,30,0,0));
        legend.setPadding(RectangleInsets.ZERO_INSETS);
//        legend.setLegendItemGraphicPadding(RectangleInsets.ZERO_INSETS);
        legend.setLegendItemGraphicPadding(new RectangleInsets(0,20,0,0));
        return legend;
    }

    public static JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                "",  // chart title
                "",  // domain axis label
                "",  // range axis label
                dataset,                     // data
                PlotOrientation.VERTICAL,    // the plot orientation
                false,                       // legend
                true,                        // tooltips
                false                        // urls
                );
        TextTitle title = new TextTitle("Grazing practices", TITLE_FONT);
        title.setPadding(new RectangleInsets(10,0,0,0));
        chart.setTitle(title);
        chart.addLegend(createLegend());
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setOutlineVisible(false);
        plot.setAxisOffset(new RectangleInsets(0,0,0,0));
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.gray);
        plot.setRangeGridlineStroke(new BasicStroke(2));

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setAutoTickUnitSelection(true);
        rangeAxis.setTickUnit(new NumberTickUnit(20));
        rangeAxis.setAxisLineVisible(true);
        rangeAxis.setLabel("% of graziers");
        rangeAxis.setAxisLineStroke(new BasicStroke(2));
        rangeAxis.setAxisLinePaint(Color.black);
        rangeAxis.setTickMarksVisible(false);
        rangeAxis.setLabelPaint(AXIS_LABEL_COLOR);
        rangeAxis.setLabelFont(AXIS_LABEL_FONT);
        rangeAxis.setLabelInsets(new RectangleInsets(0,0,0,-10));
        rangeAxis.setUpperMargin(0);
        rangeAxis.setAutoRange(false);
        rangeAxis.setRange(new Range(0,100));

        CategoryAxis cAxis = plot.getDomainAxis();
        cAxis.setTickMarksVisible(false);
        cAxis.setAxisLinePaint(Color.black);
        cAxis.setAxisLineStroke(new BasicStroke(2));
        cAxis.setLabel("");
        cAxis.setTickLabelsVisible(true);
        cAxis.setUpperMargin(0.05);
        cAxis.setLowerMargin(0.05);
        cAxis.setTickLabelFont(CAXIS_LABEL_FONT);
        cAxis.setTickLabelPaint(Color.black);
        CustomBarRenderer renderer = new CustomBarRenderer();
        plot.setRenderer(renderer);
        renderer.setDrawBarOutline(false);
        renderer.setBaseItemLabelsVisible(false);
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setMaximumBarWidth(0.08);
        renderer.setItemMargin(0.01);
        return chart;
    }
}

