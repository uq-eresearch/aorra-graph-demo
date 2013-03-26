package ereefs.charts;

import static java.lang.Math.PI;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.text.TextUtilities;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

import ereefs.boxrenderer.ImageRenderBox;
import ereefs.boxrenderer.ImageRenderer;
import ereefs.boxrenderer.Margin;
import ereefs.boxrenderer.TableBox;
import ereefs.boxrenderer.TableCellBox;
import ereefs.boxrenderer.TableCellBox.VAlign;
import ereefs.boxrenderer.TableRowBox;
import ereefs.boxrenderer.TextBox;

public class ProgressChart implements Dimensions {

    public enum Align {
        LEFT, RIGHT
    }

    private static final Color PROGRESS_COLOR = new Color(24,62,115);
    private static final int BAR_LENGTH = 250;
    private static final int BAR_HEIGHT = BAR_LENGTH*8/100;
    
    private Dimension dimension;

    private String progressLabel;
    private Align progressLabelAlign;
    private Color progressLabelColor;

    private float progress;

    private String topLeftLabel;
    private String topRightLabel;
    private String bottomLeftLabel;
    private String bottomRightLabel;

    @Override
    public void draw(Graphics2D g2, Rectangle2D area) {
        Graphics2D g0 = (Graphics2D)g2.create((int)area.getX(),
                (int)area.getY(), (int)area.getWidth(), (int)area.getHeight());
        try {
            TableBox table = getChartBox();
            table.render(g0);
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            g0.dispose();
        }
    }

    private TableBox getChartBox() {
        registerFonts();
        Font largeFont = getLargeFont();
        Font regularFont = getRegularFont();
        TableCellBox cellTL = new TableCellBox(new TextBox(topLeftLabel, largeFont));
        cellTL.setWidth(BAR_LENGTH/10*9);
        // Push TL text up a little
        cellTL.getMargin().setBottom(3);
        TableCellBox cellTR = new TableCellBox(new TextBox(topRightLabel, regularFont));
        cellTR.setAlign(TableCellBox.Align.CENTER);
        cellTR.setValign(VAlign.BOTTOM);
        // Avoid TR text truncation
        cellTR.getMargin().setRight(5);
        TableCellBox cellBL = new TableCellBox(new TextBox(bottomLeftLabel, regularFont));
        TableCellBox cellBR = new TableCellBox(new TextBox(bottomRightLabel, regularFont));
        cellBR.setAlign(TableCellBox.Align.CENTER);
        TableRowBox row1 = new TableRowBox();
        TableRowBox row2 = new TableRowBox();
        TableRowBox row3 = new TableRowBox();
        row1.addCell(cellTL);
        row1.addCell(cellTR);
        row3.addCell(cellBL);
        row3.addCell(cellBR);
        TableCellBox progressBarBox = new TableCellBox(new ImageRenderBox(new ImageRenderer() {
            @Override
            public Dimension getDimension(Graphics2D g2) throws Exception {
                return new Dimension(BAR_LENGTH+2, BAR_HEIGHT+2);
            }

            @Override
            public void render(Graphics2D g2) throws Exception {
                drawBar(g2);
            }}));
        progressBarBox.setColspan(2);
        progressBarBox.setMargin(new Margin(0,10,0,10));
        TableBox table = new TableBox();
        row2.addCell(progressBarBox);
        table.addRow(row1);
        table.addRow(row2);
        table.addRow(row3);
        return table;
    }

    private void drawBar(Graphics2D g2) {
        g2.translate(1, 1);
        Shape barShape = createBarShape(BAR_LENGTH, BAR_HEIGHT);
        Shape progressShape = createProgressShape(BAR_LENGTH, BAR_HEIGHT);
        g2.setColor(Color.white);
        g2.fill(barShape);
        g2.setColor(PROGRESS_COLOR);
        g2.fill(progressShape);
        g2.setColor(Color.black);
        g2.draw(barShape);
        g2.setColor(Color.white);
        g2.setFont(getBarFont());
        if(progressLabel!=null) {
            if(progressLabelColor != null) {
                g2.setColor(progressLabelColor);
            } else {
                g2.setColor(Color.white);
            }
            float x;
            TextAnchor anchor;
            if(progressLabelAlign == null || progressLabelAlign == Align.LEFT) {
                x = BAR_HEIGHT / 2;
                anchor = TextAnchor.CENTER_LEFT;
            } else {
                x = BAR_LENGTH - BAR_HEIGHT / 2f;
                anchor = TextAnchor.CENTER_RIGHT;
            }
            TextUtilities.drawAlignedString(String.format("%s", progressLabel),
                    g2, x, BAR_HEIGHT/2f, anchor);
        }
    }

    private Shape createBarShape(double width, double height) {
        Path2D.Double barShape = new Path2D.Double();
        double capR = height / 2;
        double rx = width-capR;
        GraphUtils.addArc(barShape, true, capR, -PI/2, -PI, capR, capR);
        GraphUtils.addArc(barShape, false, capR, -PI, -3*PI/2, capR, capR);
        barShape.lineTo(rx, barShape.getCurrentPoint().getY());
        GraphUtils.addArc(barShape, false, capR, PI/2, 0, rx, capR);
        GraphUtils.addArc(barShape, false, capR, 0, -PI/2, rx, capR);
        barShape.closePath();
        return barShape;
    }

    private Shape createProgressShape(double width, double height) {
        Path2D.Double barShape = new Path2D.Double();
        double capR = height / 2;
        double rx = width-capR;
        double progressLength = min(progress/100*width, width);
        if(progressLength < capR) {
            double alpha = Math.asin((capR-progressLength)/capR);
            GraphUtils.addArc(barShape, true, capR, -PI/2-alpha, -PI, capR, capR);
            GraphUtils.addArc(barShape, false, capR, -PI, -3*PI/2+alpha, capR, capR);
        } else {
            GraphUtils.addArc(barShape, true, capR, -PI/2, -PI, capR, capR);
            GraphUtils.addArc(barShape, false, capR, -PI, -3*PI/2, capR, capR);
            if(progressLength < (width-capR)) {
                barShape.lineTo(progressLength, barShape.getCurrentPoint().getY());
                barShape.lineTo(progressLength, 0);
            } else {
                barShape.lineTo(rx, barShape.getCurrentPoint().getY());
                 if(progressLength < width) {
                     double px = progressLength - (width - capR);
                     double alpha = Math.asin(px/capR);
                     GraphUtils.addArc(barShape, false, capR, PI/2, PI/2-alpha, rx, capR);
                     barShape.lineTo(barShape.getCurrentPoint().getX(), 
                             barShape.getCurrentPoint().getY() - 2*Math.cos(alpha)*capR);
                     GraphUtils.addArc(barShape, false, capR, -PI/2+alpha, -PI/2, rx, capR);
                } else {
                  GraphUtils.addArc(barShape, false, capR, PI/2, 0, rx, capR);
                  GraphUtils.addArc(barShape, false, capR, 0, -PI/2, rx, capR);
                }
            }
        }
        barShape.closePath();
        return barShape;
    }

    public Dimension getMinDimension(Graphics2D g2) throws Exception {
        return getChartBox().getDimension(g2);
    }

    public Dimension getMinDimension() throws Exception {
        BufferedImage img0 = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img0.createGraphics();
        try {
            return getMinDimension(g2);
        } finally {
            g2.dispose();
        }
    }

    @Override
    public Dimension getDimension() {
        return dimension;
    }

    @Override
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }


    public String getProgressLabel() {
        return progressLabel;
    }

    public void setProgressLabel(String progressLabel) {
        this.progressLabel = progressLabel;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public String getTopLeftLabel() {
        return topLeftLabel;
    }

    public void setTopLeftLabel(String topLeftLabel) {
        this.topLeftLabel = topLeftLabel;
    }

    public String getTopRightLabel() {
        return topRightLabel;
    }

    public void setTopRightLabel(String topRightLabel) {
        this.topRightLabel = topRightLabel;
    }

    public String getBottomLeftLabel() {
        return bottomLeftLabel;
    }

    public void setBottomLeftLabel(String bottomLeftLabel) {
        this.bottomLeftLabel = bottomLeftLabel;
    }

    public String getBottomRightLabel() {
        return bottomRightLabel;
    }

    public void setBottomRightLabel(String bottomRightLabel) {
        this.bottomRightLabel = bottomRightLabel;
    }

    protected void registerFonts() {
        final InputStream fontStream1 = getClass().getResourceAsStream(
                "LiberationSans-Regular.ttf");
        final InputStream fontStream2 = getClass().getResourceAsStream(
                "LiberationSans-Bold.ttf");
        final GraphicsEnvironment ge =
                GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,fontStream1));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,fontStream2));
        } catch (Exception e) {
            // It's in the classpath, so failure should never happen
            throw new RuntimeException(e);
        }
    }

    protected Font getRegularFont() {
        return new Font("Liberation Sans", Font.PLAIN, BAR_HEIGHT/5*3);
    }

    protected Font getLargeFont() {
        return new Font("Liberation Sans", Font.BOLD, BAR_HEIGHT/5*4);
    }

    protected Font getBarFont() {
        return new Font("Liberation Sans", Font.BOLD, BAR_HEIGHT/10*7);
    }

    public Align getProgressLabelAlign() {
        return progressLabelAlign;
    }

    public void setProgressLabelAlign(Align progressLabelAlign) {
        this.progressLabelAlign = progressLabelAlign;
    }

    public Color getProgressLabelColor() {
        return progressLabelColor;
    }

    public void setProgressLabelColor(Color progressLabelColor) {
        this.progressLabelColor = progressLabelColor;
    }


    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            private TableCellBox getProgressChart(float progress, String progressLabel, String tl,
                    String tr, String bl, String br, Align plAlign, Color plColor) {
                final ProgressChart chart = new ProgressChart();
                chart.setProgress(progress);
                chart.setProgressLabel(progressLabel);
                chart.setTopLeftLabel(tl);
                chart.setTopRightLabel(tr);
                chart.setBottomLeftLabel(bl);
                chart.setBottomRightLabel(br);
                chart.setProgressLabelAlign(plAlign);
                chart.setProgressLabelColor(plColor);
                TableCellBox progressBarBox = new TableCellBox(new ImageRenderBox(new ImageRenderer() {
                    @Override
                    public Dimension getDimension(Graphics2D g2) throws Exception {
                        return chart.getMinDimension(g2);
                    }

                    @Override
                    public void render(Graphics2D g2) throws Exception {
                        Dimension d = getDimension(g2);
                        Rectangle2D.Double rect = new Rectangle2D.Double(0,0, d.width, d.height);
                        chart.draw(g2, rect);
                    }}));
                return progressBarBox;
            }

            private TableBox buildTable() {
                TableBox table = new TableBox();
                {
                    TableRowBox row = new TableRowBox();
                    row.addCell(getProgressChart(
                        1.5f, "1.5%", "Horticulture", "2013 target", "0%", "100%", Align.LEFT, Color.white));
                    row.addCell(getProgressChart(
                        33f, "33%", "Horticulture", "2013 target", "0%", "100%", Align.LEFT, Color.white));
                    row.addCell(getProgressChart(
                        98.5f, "98.5%", "Horticulture", "2013 target", "0%", "100%", Align.LEFT, Color.white));
                    table.addRow(row);
                }
                {
                    TableRowBox row = new TableRowBox();
                    row.addCell(getProgressChart(
                        1.5f, "1.5%", "Horticulture", "2013 target", "0%", "100%", Align.LEFT, Color.black));
                    row.addCell(getProgressChart(
                        33f, "33%", "Horticulture", "2013 target", "0%", "100%", Align.LEFT, Color.black));
                    row.addCell(getProgressChart(
                        98.5f, "98.5%", "Horticulture", "2013 target", "0%", "100%", Align.LEFT, Color.black));
                    table.addRow(row);
                }
                {
                    TableRowBox row = new TableRowBox();
                    row.addCell(getProgressChart(
                        1.5f, "1.5%", "Horticulture", "2013 target", "0%", "100%", Align.RIGHT, Color.white));
                    row.addCell(getProgressChart(
                        33f, "33%", "Horticulture", "2013 target", "0%", "100%", Align.RIGHT, Color.white));
                    row.addCell(getProgressChart(
                        98.5f, "98.5%", "Horticulture", "2013 target", "0%", "100%", Align.RIGHT, Color.white));
                    table.addRow(row);
                }
                {
                    TableRowBox row = new TableRowBox();
                    row.addCell(getProgressChart(
                        1.5f, "1.5%", "Horticulture", "2013 target", "0%", "100%", Align.RIGHT, Color.black));
                    row.addCell(getProgressChart(
                        33f, "33%", "Horticulture", "2013 target", "0%", "100%", Align.RIGHT, Color.black));
                    row.addCell(getProgressChart(
                        98.5f, "98.5%", "Horticulture", "2013 target", "0%", "100%", Align.RIGHT, Color.black));
                    table.addRow(row);
                }
                return table;
            }

            public void run() {
                final TableBox table = buildTable();
                table.getMargin().setSize(5);
                JFrame frame = new JFrame("Progress Chart Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new JPanel() {
                    {
                        setBorder(BorderFactory.createLineBorder(Color.black));
                    }

                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(1000, 500);
                    }

                    @Override
                    public void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        try {
                            Dimension d = table.getDimension((Graphics2D)g);
                            Graphics2D g2 = (Graphics2D)((Graphics2D)g).create(0, 0, d.width, d.height);
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            table.render(g2);
                            g2.dispose();
                        } catch(Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                //Display the window.
                frame.pack();
                RefineryUtilities.centerFrameOnScreen(frame);
                frame.setVisible(true);
            }
        });
    }

}
