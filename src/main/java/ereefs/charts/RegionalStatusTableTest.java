package ereefs.charts;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import ereefs.boxrenderer.ImageRenderBox;
import ereefs.boxrenderer.ImageRenderer;
import ereefs.boxrenderer.TableCellBox;

public class RegionalStatusTableTest extends ChartTest {

    @Override
    protected TableCellBox buildChartBox() {
        final RegionalStatusTable table = new RegionalStatusTable();
        TableCellBox chartBox = new TableCellBox(new ImageRenderBox(new ImageRenderer() {
            @Override
            public Dimension getDimension(Graphics2D g2) throws Exception {
                return table.getMinDimension(g2);
            }

            @Override
            public void render(Graphics2D g2) throws Exception {
                Dimension d = getDimension(g2);
                Rectangle2D.Double rect = new Rectangle2D.Double(0,0, d.width, d.height);
                table.draw(g2, rect);
            }}));
        return chartBox;
    }

    protected String getTitle() {
        return "Regional status table test";
    }

    public static void main(String[] args) throws Exception {
        new RegionalStatusTableTest().run();
    }

}
