package ereefs.charts;

import static ereefs.charts.Configuration.AXIS_LABEL_COLOR;
import static ereefs.charts.Configuration.AXIS_LABEL_FONT;
import static ereefs.charts.Configuration.COLOR_A;
import static ereefs.charts.Configuration.COLOR_A_TRANS;
import static ereefs.charts.Configuration.COLOR_B;
import static ereefs.charts.Configuration.COLOR_B_TRANS;
import static ereefs.charts.Configuration.COLOR_C;
import static ereefs.charts.Configuration.COLOR_C_TRANS;
import static ereefs.charts.Configuration.COLOR_D;
import static ereefs.charts.Configuration.COLOR_D_TRANS;
import static ereefs.charts.Configuration.LEGEND_FONT;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

import org.apache.commons.lang3.tuple.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import com.google.common.collect.ImmutableList;

public class LandPracticeChart {

    private GroupedStackedBarRenderer renderer;

    private static LegendTitle createLegend() {
        final LegendItemCollection legendItems = new LegendItemCollection();
        FontRenderContext frc = new FontRenderContext(null, true, true);
        GlyphVector gv = LEGEND_FONT.createGlyphVector(frc, new char[] {'X'});
        Shape shape = gv.getGlyphVisualBounds(0);
        for(Pair<String, Color> p : ImmutableList.of(
                Pair.of("A", COLOR_A), Pair.of("B", COLOR_B),
                Pair.of("C", COLOR_C), Pair.of("D", COLOR_D))) {
            LegendItem li = new LegendItem(p.getLeft(), null, null, null, shape, p.getRight());
            li.setLabelFont(LEGEND_FONT);
            legendItems.add(li);
        }
        LegendTitle legend = new LegendTitle(new LegendItemSource() {
            @Override
            public LegendItemCollection getLegendItems() {
                return legendItems;
            }});
        legend.setPosition(RectangleEdge.BOTTOM);
        return legend;
    }

    public JFreeChart createChart(CategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createStackedBarChart(
            "",  // chart title
            "",  // domain axis label
            "",  // range axis label
            dataset,                     // data
            PlotOrientation.VERTICAL,    // the plot orientation
            false,                       // legend
            true,                        // tooltips
            false                        // urls
        );
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
//        rangeAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
//        rangeAxis.setAutoTickUnitSelection(false);
        rangeAxis.setAutoTickUnitSelection(true);
        rangeAxis.setTickUnit(new NumberTickUnit(20));
        rangeAxis.setAxisLineVisible(true);
        rangeAxis.setLabel("% of landholders");
        rangeAxis.setAxisLineStroke(new BasicStroke(2));
        rangeAxis.setAxisLinePaint(Color.black);
        rangeAxis.setTickMarksVisible(false);
        rangeAxis.setLabelPaint(AXIS_LABEL_COLOR);
        rangeAxis.setLabelFont(AXIS_LABEL_FONT);
        rangeAxis.setLabelInsets(new RectangleInsets(0,0,0,-10));
        rangeAxis.setUpperMargin(0);
        
        CategoryAxis cAxis = plot.getDomainAxis();
        cAxis.setTickMarksVisible(false);
        cAxis.setAxisLinePaint(Color.black);
        cAxis.setAxisLineStroke(new BasicStroke(2));
        cAxis.setLabel("");
        cAxis.setTickLabelsVisible(false);
        cAxis.setCategoryMargin(0.05);
        cAxis.setUpperMargin(0.1);
        cAxis.setLowerMargin(0);
        GroupedStackedBarRenderer renderer;
        if(this.renderer == null) {
            renderer = new GroupedStackedBarRenderer();
        } else {
            renderer = this.renderer;
        }
        plot.setRenderer(renderer);
        renderer.setSeriesPaint(0, COLOR_A_TRANS);
        renderer.setSeriesPaint(1, COLOR_B_TRANS);
        renderer.setSeriesPaint(2, COLOR_C_TRANS);
        renderer.setSeriesPaint(3, COLOR_D_TRANS);
        renderer.setSeriesPaint(4, COLOR_A);
        renderer.setSeriesPaint(5, COLOR_B);
        renderer.setSeriesPaint(6, COLOR_C);
        renderer.setSeriesPaint(7, COLOR_D);
        renderer.setRenderAsPercentages(true);
        renderer.setDrawBarOutline(false);
        renderer.setBaseItemLabelsVisible(false);
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardBarPainter());
//        renderer.setMaximumBarWidth(0.125);
        renderer.setItemMargin(0.10);
        KeyToGroupMap map = new KeyToGroupMap();
        map.mapKeyToGroup("A_08", "G1");
        map.mapKeyToGroup("B_08", "G1");
        map.mapKeyToGroup("C_08", "G1");
        map.mapKeyToGroup("D_08", "G1");
        map.mapKeyToGroup("A_09", "G2");
        map.mapKeyToGroup("B_09", "G2");
        map.mapKeyToGroup("C_09", "G2");
        map.mapKeyToGroup("D_09", "G2");
        renderer.setSeriesToGroupMap(map);
        return chart;
    }

    public GroupedStackedBarRenderer getRenderer() {
        return renderer;
    }

    public void setRenderer(GroupedStackedBarRenderer renderer) {
        this.renderer = renderer;
    }

}
