package ereefs.charts;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;

public class ChartRenderer {

    private Dimensions chart;

    private Dimension scale;

    public ChartRenderer(Dimensions chart) {
        this.chart = chart;
    }

    public BufferedImage render() throws Exception {
        Dimension d = chart.getDimension();
        if(d == null) {
            throw new Exception("missing dimension on chart " + chart);
        }
        BufferedImage img0 = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img0.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        chart.draw(g2, new Rectangle2D.Double(0, 0, d.width, d.height));
        g2.dispose();
        if(scale!=null) {
            img0 = scale(img0);
        }
        return img0;
    }

    public BufferedImage scale(BufferedImage src) throws Exception {
        return Scalr.resize(
                src, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, scale.width, scale.height);
    }

    public void setScale(Dimension scale) {
        this.scale = scale;
    }

}
