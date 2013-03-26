package ereefs.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.jhlabs.image.GaussianFilter;


public class GraphUtils {

    public static enum TextAnchor {
        BASELINE, CENTER;
    }

    private Graphics2D g;

    public GraphUtils(Graphics2D g) {
        this.g = g;
    }

    public void drawCircle(int x, int y, float radius) {
        Rectangle r = getBoundingBoxCircle(x, y, radius);
        g.drawOval(r.x, r.y, r.width, r.height);
    }

    public void fillCircle(Point2D center, float radius) {
        fillCircle((int)center.getX(), (int)center.getY(), radius);
    }

    public void fillCircle(int x, int y, float radius) {
        Rectangle r = getBoundingBoxCircle(x, y, radius);
        g.fillOval(r.x, r.y, r.width, r.height);
    }

    public void fillArc(int x, int y, float radius, int startAngle, int arcAngle) {
        Rectangle r = getBoundingBoxCircle(x, y, radius);
        g.fillArc(r.x, r.y, r.width, r.height, startAngle, arcAngle);
    }

    public void drawArc(int x, int y, float radius, int startAngle, int arcAngle) {
        this.drawArc(x,y,radius, startAngle, arcAngle, Arc2D.PIE);
    }

    public void drawArc(int x, int y, float radius, int startAngle, int arcAngle, int type) {
        Rectangle r = getBoundingBoxCircle(x, y, radius);
        Arc2D.Double arc = new Arc2D.Double(r.x, r.y, r.width, r.height, startAngle, arcAngle, type);
        g.draw(arc);
    }

    public Rectangle getBoundingBoxCircle(int x, int y, float radius) {
        Rectangle r = new Rectangle();
        r.x = x - (int)radius;
        r.y = y - (int)radius;
        int diameter = (int)(2 * radius);
        r.height = diameter;
        r.width = diameter;
        return r;
    }

    public static double toRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    public static double toDegrees(double radians) {
        return radians * 180 / Math.PI;
    }

//  copied from http://www.java.happycodings.com/Java2D/code11.html and modified to also
//  support:
//     - counter clockwise text rendering (in this case the text is turned upside down)
//     - text alignment is centre
    public void drawCircleText(String st, int x, int y, double radius, double a1,
            boolean clockwise, TextAnchor anchor)
    {
        double theta = a1;
        // align centre
        if(clockwise) {
            theta -= getCircleTextLengthInRadians(st, radius) / 2;
        } else {
            theta += getCircleTextLengthInRadians(st, radius) / 2;
        }
        char ch[] = st.toCharArray();
        Font font = g.getFont();
        FontRenderContext frc = g.getFontRenderContext();
        FontMetrics fm = g.getFontMetrics();
        for(int i = 0; i < ch.length; i++) {
            GlyphVector gv = font.createGlyphVector(frc, Character.toString(ch[i]));
            Shape glyph = gv.getGlyphOutline(0);
            double posY = -radius;
            if(anchor.equals(TextAnchor.CENTER)) {
                posY+=getCapHeight()/2;
            }
            AffineTransform transform = new AffineTransform();
            transform.concatenate(AffineTransform.getTranslateInstance(x, y));
            transform.concatenate(AffineTransform.getRotateInstance(theta, 0.0, 0.0));
            transform.concatenate(AffineTransform.getTranslateInstance(-getCharWidth(ch[i],fm)/2.0, posY));
            // turn glyph by 180 degree and keep same baseline
            if(!clockwise) {
                transform.concatenate(AffineTransform.getRotateInstance(toRadians(180), getCharWidth(ch[i],fm)/2.0, -getCapHeight()/2));
            }
            glyph = transform.createTransformedShape(glyph);
            g.fill(glyph);
            if (i < (ch.length - 1)) {
                double adv = getCharWidth(ch[i],fm)/2.0 + fm.getLeading() + getCharWidth(ch[i + 1],fm)/2.0;
                if(clockwise) {
                    theta += Math.sin(adv / radius);
                } else {
                    theta -= Math.sin(adv / radius);
                }
            }
        }
    }

    private static int getCharWidth(char c, FontMetrics fm) {
        if (c == ' ' || Character.isSpaceChar(c)) {
            return fm.charWidth(' ');
//            return fm.charWidth('n');
        }
        else {
            return fm.charWidth(c);
        }
    }

    public double getCapHeight() {
        Font font = g.getFont();
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector gv = font.createGlyphVector(frc, "A");
        Shape glyph = gv.getGlyphOutline(0);
        Rectangle2D rec = glyph.getBounds2D();
        return rec.getHeight();
    }

    private double getCircleTextLengthInRadians(String st, double radius) {
        double theta = 0;
        char ch[] = st.toCharArray();
        FontMetrics fm = g.getFontMetrics();
        for(int i = 0; i < ch.length; i++) {
            if (i < (ch.length - 1)) {
                double adv = getCharWidth(ch[i],fm)/2.0 + fm.getLeading() + getCharWidth(ch[i + 1],fm)/2.0;
                theta += Math.sin(adv / radius);
            }
        }
        return theta;
    }

    public Graphics2D getGraphics() {
        return g;
    }

    public void drawImage(Image image, int x, int y) {
        g.drawImage(image, x-image.getWidth(null)/2, y-image.getHeight(null)/2, null);
    }

    public void setStroke(float width) {
        g.setStroke(new BasicStroke(width));
    }

    public void setColor(Color color) {
        g.setColor(color);
    }

    private void drawGlowString(String label, Font font, Color glowColor,
            Color fontColor, float glowRadius, BufferedImage img1, int xpad, int ypad) {
        BufferedImage img0 = new BufferedImage(img1.getWidth(), img1.getHeight(), img1.getType());
        {
            Graphics2D g0 = img0.createGraphics();
            g0.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g0.setColor(glowColor);
            g0.setFont(font);
            g0.drawString(label, xpad, ypad);
            g0.dispose();
        }
        {
            Graphics2D g1 = img1.createGraphics();
            g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // I have copied the GaussianFilter from the jhlabs page at
            // http://www.jhlabs.com/ip/blurring.html
            // According to the Disclaimer the source code can be freely used.
            GaussianFilter filter = new GaussianFilter(glowRadius);
            g1.drawImage(img0, filter, 0, 0);
            g1.setColor(fontColor);
            g1.setFont(font);
            g1.drawString(label, xpad, ypad);
            // TODO DEBUG
//            g1.setColor(Color.blue);
//            g1.drawRect(0, 0, img1.getWidth()-1, img1.getHeight()-1);
            g1.dispose();
        }
    }

    public BufferedImage drawGlowString(String label, Font font,
            Color glowColor, Color fontColor, float glowRadius) {
        g.setFont(font);
        FontRenderContext frc = g.getFontRenderContext();
        FontMetrics fm = g.getFontMetrics();
        GlyphVector gv = font.createGlyphVector(frc, label);
        Rectangle2D bounds = gv.getVisualBounds();
        BufferedImage img = new BufferedImage(
                (int)(bounds.getWidth()+glowRadius+5), fm.getHeight(), BufferedImage.TYPE_INT_ARGB);
        drawGlowString(label, font, glowColor, fontColor, glowRadius, img, (int)glowRadius/2, fm.getAscent());
        return img;
    }

    public static BufferedImage getImage(String name) throws IOException {
        InputStream in = GraphUtils.class.getClassLoader().getResourceAsStream(name);
        if(in == null) {
            throw new IOException(String.format("Resource '%s' not found", name));
        }
        return ImageIO.read(in);
    }

    public static BufferedImage createStripedTexture(Color color) {
        return createHatchingTexture(color, 5, 5);
    }

    public static BufferedImage createHatchingTexture(Paint paint, int dash1, int dash2) {
        int size = dash1+dash2;
        BufferedImage result = new BufferedImage(size,size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)result.getGraphics();
        g2.setPaint(paint);
        for(int row =0;row<result.getHeight();row++) {
            g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
                    new float[] {dash1,dash2}, row));
            g2.drawLine(0, row, size, row);
        }
        return result;
    }

    /**
     *  Cubic bezier approximation of a circular arc centered at the origin, 
     *  from (radians) a1 to a2, where a2-a1 < pi/2.  The arc's radius is r.
     * 
     *  Returns an array of four points, where x1,y1 and x4,y4 are the arc's end points
     *  and x2,y2 and x3,y3 are the cubic bezier's control points.
     * 
     *  This algorithm is based on the approach described in:
     *  A. Riškus, "Approximation of a Cubic Bezier Curve by Circular Arcs and Vice Versa," 
     *  Information Technology and Control, 35(4), 2006 pp. 371-378.
     *  
     *  Copied from PathArcUtils.as 
     *  http://hansmuller-flex.blogspot.com.au/2011/10/more-about-approximating-circular-arcs.html
     *  and translated into java
     *  
     * This work is licensed under the Creative Commons Attribution 3.0
     * Unported License. To view a copy of this license, visit
     * http://creativecommons.org/licenses/by/3.0/ or send a letter to
     * Creative Commons, 444 Castro Street, Suite 900, Mountain View,
     * California, 94041, USA.
     */
    // also read: http://www.whizkidtech.redprince.net/bezier/circle/
    public static Point2D.Double[] createSmallArc(double r, double a1, double a2) {
        Point2D.Double[] result = new Point2D.Double[4];
        // Compute all four points for an arc that subtends the same total angle
        // but is centered on the X-axis
        double a = (a2 - a1) / 2.0;
        double x4 = r * Math.cos(a);
        double y4 = r * Math.sin(a);
        double x1 = x4;
        double y1 = -y4;
        
        double q1 = x1*x1 + y1*y1;
        double q2 = q1 + x1*x4 + y1*y4;
        double k2 = 4d/3d * (Math.sqrt(2 * q1 * q2) - q2) / (x1 * y4 - y1 * x4);
        
        double x2 = x1 - k2 * y1;
        double y2 = y1 + k2 * x1;
        double x3 = x2; 
        double y3 = -y2;
        
        // Find the arc points' actual locations by computing x1,y1 and x4,y4 
        // and rotating the control points by a + a1
        
        double ar = a + a1;
        double cos_ar = Math.cos(ar);
        double sin_ar = Math.sin(ar);
        
        result[0] = new Point2D.Double(r * Math.cos(a1), r * Math.sin(a1));
        result[1] = new Point2D.Double(x2 * cos_ar - y2 * sin_ar, x2 * sin_ar + y2 * cos_ar);
        result[2] = new Point2D.Double(x3 * cos_ar - y3 * sin_ar, x3 * sin_ar + y3 * cos_ar);
        result[3] = new Point2D.Double(r * Math.cos(a2), r * Math.sin(a2));
        return result;
    }

    /**
     * Adds an arc to the path. The arc must be smaller or equal to pi/2 (90°)
     * @param path - the path to add the arc to
     * @param moveTo - if true adds a path.moveTo to the beginning of the arc 
     * @param r - the radius of the arc
     * @param a1 - the start angle in radians
     * @param a2 - the end angle in radians
     * @param x - translate by x on the x axis 
     * @param y - translate by y on the y axis
     */
    public static Path2D addArc(Path2D path, boolean moveTo, double r, double a1, double a2, double x, double y) {
        Point2D.Double[] arc = createSmallArc(r, a1, a2);
        if(moveTo) {
            path.moveTo(arc[0].x+x, arc[0].y+y);
        }
        path.curveTo(arc[1].x+x, arc[1].y+y, arc[2].x+x, arc[2].y+y, arc[3].x+x, arc[3].y+y);
        return path;
    }

}
