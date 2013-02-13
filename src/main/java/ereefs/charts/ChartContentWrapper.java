package ereefs.charts;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import ereefs.content.Content;

public class ChartContentWrapper implements Content {

    private ChartRenderer renderer;

    public ChartContentWrapper(ChartRenderer renderer) {
        this.renderer = renderer;
    }

    public ChartContentWrapper(Dimensions chart) {
        renderer = new ChartRenderer(chart);
    }

    @Override
    public String getContentType() {
        return "image/png";
    }

    @Override
    public void write(OutputStream out) throws Exception {
        BufferedImage img = renderer.render();
        ImageIO.write(img, "png", out);
    }

}
