package ereefs.charts;

import static ereefs.charts.Configuration.LEGEND_FONT;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.category.CategoryDataset;

import com.google.common.collect.ImmutableMap;

public class BarLegendRenderer extends GroupedStackedBarRenderer {

    private Map<Pair<Integer, Integer>, String> BAR_LEGEND = 
            new ImmutableMap.Builder<Pair<Integer, Integer>, String>()
            .put(Pair.of(3, 0), "Nutrients 2008-2009")
            .put(Pair.of(7, 0), "Nutrients 2009-2010")
            .put(Pair.of(3, 1), "Herbicides 2008-2009")
            .put(Pair.of(7, 1), "Herbicides 2009-2010")
            .put(Pair.of(3, 2), "Soil 2008-2009")
            .put(Pair.of(7, 2),"Soil 2009-2010").build();

    @Override
    public void drawItem(Graphics2D g2, CategoryItemRendererState state,
            Rectangle2D dataArea, CategoryPlot plot,
            CategoryAxis domainAxis, ValueAxis rangeAxis,
            CategoryDataset dataset, int row, int column, int pass) {
        super.drawItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row,
                column, pass);
//        System.out.println(String.format("row %s, column %s, pass %s", row, column, pass));
        // after the stacked bar is completely rendered draw the glow text into it.
        if((pass == 2) && ((row == 3) || (row == 7))) {
            String label = BAR_LEGEND.get(Pair.of(row, column));
            double barW0 = calculateBarW0(plot, plot.getOrientation(), dataArea, domainAxis,
                    state, row, column);
            double labelx = barW0 + state.getBarWidth()/2;
            // TODO does not seem to be correct, but ok for now
            double labely = dataArea.getMinY()+dataArea.getHeight()*.10;
            double angle = GraphUtils.toRadians(-90);
            g2.setFont(LEGEND_FONT);
            g2.setColor(Color.WHITE);
            GraphUtils g = new GraphUtils(g2);
            BufferedImage img = g.drawGlowString(
                    label, LEGEND_FONT, Color.black, Color.white, 6);
            AffineTransform saveT = g2.getTransform();
            AffineTransform transform = new AffineTransform();
            // jfree chart seem to be using the transform on the Graphics2D object 
            // for scaling when the window gets very small or large
            // therefore we can not just overwrite the transform but have to factor it into 
            // our rotation and translation transformations.
            transform.concatenate(saveT);
            transform.concatenate(AffineTransform.getRotateInstance(angle, labelx, labely));
            // first translate to the center right
            transform.concatenate(AffineTransform.getTranslateInstance(-img.getWidth(), -img.getHeight()/2));
            g2.setTransform(transform);
            g2.drawImage(img, null, (int)labelx, (int)labely);
            g2.setTransform(saveT);
        }
        if((pass == 2) && (row == 7)) {
            // Workaround: because the dataArea sits on the the Axis the 0% gridline gets drawn 
            // over the category axis making it gray. To fix this as we draw another black line
            // to restore the black axis.
            g2.setColor(Color.black);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine((int)dataArea.getMinX(), (int)dataArea.getMaxY(), (int)dataArea.getMaxX(), (int)dataArea.getMaxY());
            g2.drawLine((int)dataArea.getMinX(), (int)dataArea.getMinY(), (int)dataArea.getMinX(), (int)dataArea.getMaxY());
        }
    }

}
