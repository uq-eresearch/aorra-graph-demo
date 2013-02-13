package ereefs.images;

import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.strip;

import java.awt.image.BufferedImage;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import ereefs.content.Content;

public class ImageContent implements Content {

    private ImageRenderer renderer;

    private String scale;

    public ImageContent(ImageRenderer renderer) {
        this.renderer = renderer;
    }

    public ImageContent(ImageRenderer renderer, String scale) {
        this(renderer);
        this.scale = scale;
    }

    @Override
    public String getContentType() {
        return "image/png";
    }

    @Override
    public void write(OutputStream out) throws Exception {
        BufferedImage img = renderer.render();
        if(img != null) {
            ImageIO.write(scale(img), "png", out);
        } else {
            throw new Exception("no image rendered");
        }
    }

    private BufferedImage scale(BufferedImage img) {
        String width = "width:";
        String height = "height:";
        String px = "px";
        String percent = "%";
        if(isBlank(scale)) {
            return img;
        } else if(startsWith(scale, width)) {
            String s = removeStart(scale, width);
            if(endsWith(s, px)) {
                s = strip(removeEnd(s, px));
                int w = Integer.parseInt(s);
                return Scalr.resize(img, Method.ULTRA_QUALITY, Mode.FIT_TO_WIDTH, w);
            } else if(endsWith(s, percent)) {
                s = strip(removeEnd(s, percent));
                int w = Math.round((Float.parseFloat(s)/100f)*img.getWidth());
                return Scalr.resize(img, Method.ULTRA_QUALITY, Mode.FIT_TO_WIDTH, w);
            }
        } else if(startsWith(scale, height)) {
            String s = removeStart(scale, height);
            if(endsWith(s, px)) {
                s = strip(removeEnd(s, px));
                int h = Integer.parseInt(s);
                return Scalr.resize(img, Method.ULTRA_QUALITY, Mode.FIT_TO_HEIGHT, h);
            } else if(endsWith(s, percent)) {
                s = strip(removeEnd(s, percent));
                int h = Math.round((Float.parseFloat(s)/100f)*img.getHeight());
                return Scalr.resize(img, Method.ULTRA_QUALITY, Mode.FIT_TO_HEIGHT, h);
            }
        }
        return img;
    }

}
