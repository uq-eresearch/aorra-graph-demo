package ereefs.boxrenderer.xhtml;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import ereefs.boxrenderer.Box;

public class BackgroundImageCssStyle extends AbstractCssStyle implements CssStyle {

    @Override
    public void style(Box box) throws Exception {
        String value = getProperty().getValue();
        if(StringUtils.startsWith(value, "inline")) {
            String imgData = StringUtils.substringBetween(value, "('", "')");
            byte[] buf = hexStringToByteArray(imgData);
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(buf));
            box.setBackgroundImage(img);
        }
    }

    //http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
