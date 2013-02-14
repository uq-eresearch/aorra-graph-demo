package ereefs.charts;

import static java.lang.Math.min;
import static java.lang.Math.round;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

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
import ereefs.boxrenderer.TableCellBox.Align;
import ereefs.boxrenderer.TableCellBox.VAlign;
import ereefs.boxrenderer.TableRowBox;
import ereefs.boxrenderer.TextBox;

public class ProgressChart implements Dimensions {

    private static final Color PROGRESS_COLOR = new Color(24,62,115);
    private static final int BAR_HEIGHT = 20;

    private static final Font FONT1 = new Font("Liberation Sans", Font.PLAIN, 12);
    private static final Font FONT2 = new Font("Liberation Sans", Font.BOLD, 16);

    private Dimension dimension;

    private String progressLabel;

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
        TableCellBox cellTL = new TableCellBox(new TextBox(topLeftLabel, FONT2));
        cellTL.setWidth(225);
        cellTL.getMargin().setBottom(3);
        TableCellBox cellTR = new TableCellBox(new TextBox(topRightLabel, FONT1));
        cellTR.setAlign(Align.CENTER);
        cellTR.setValign(VAlign.BOTTOM);
        TableCellBox cellBL = new TableCellBox(new TextBox(bottomLeftLabel, FONT1));
        TableCellBox cellBR = new TableCellBox(new TextBox(bottomRightLabel, FONT1));
        cellBR.setAlign(Align.CENTER);
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
                return new Dimension(250, BAR_HEIGHT+2);
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
        int w = g2.getClipBounds().width-1;
        GraphUtils g = new GraphUtils(g2);
        float capR = ((float)BAR_HEIGHT / 2f);
        int lx = round(capR);
        int ly = 0;
        int rx = w-round(capR);
        int ry = 0;
        int totalLength = rx-lx + round(2 * capR);
        int pLength = min(round((float)progress/(float)100*totalLength), totalLength);
        g2.setColor(Color.white);
        g.fillArc(lx, ly+Math.round(capR), capR, 90, 180);
        g2.fillRect(lx, ly, rx-lx, BAR_HEIGHT);
        g.fillArc(rx, ry+Math.round(capR), capR, -90, 180);
        g2.setColor(PROGRESS_COLOR);
        if(pLength>0) {
            g.fillArc(lx, ly+Math.round(capR), capR, 90, 180);
            // if the arc radius is bigger then the progress length then
            // draw a white box on part of the arc to overwrite some of the fill.
            // TODO might be an issue with transparency,
            // think about using a fill cord instead (see Arc2D)
            if(capR>pLength) {
                g2.setColor(Color.white);
                g2.fillRect(lx-round(capR-pLength), ly, totalLength, BAR_HEIGHT);
            }
        }
        pLength -= capR;
        int width = Math.min(pLength, rx-lx);
        if(pLength>0) {
            g2.setColor(PROGRESS_COLOR);
            g2.fillRect(lx, ly, width, BAR_HEIGHT);
        }
        pLength -=width;
        if(pLength>0) {
            g.fillArc(rx, ry+Math.round(capR), capR, -90, 180);
            if(pLength<capR) {
                g2.setColor(Color.white);
                g2.fillRect(rx+pLength, ry, Math.round(capR), BAR_HEIGHT);
            }
        }
        g.setColor(Color.black);
        g2.drawLine(lx, ly, rx, ry);
        g2.drawLine(lx, ly+BAR_HEIGHT, rx, ry+BAR_HEIGHT);
        g.drawArc(lx, ly+Math.round(capR), capR, 90, 180, Arc2D.OPEN);
        g.drawArc(rx, ry+Math.round(capR), capR, -90, 180, Arc2D.OPEN);
        g2.setColor(Color.white);

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        if(progressLabel!=null) {
            TextUtilities.drawAlignedString(String.format("%s", progressLabel),
                    g2, lx-round(capR/2), ly+(BAR_HEIGHT/2)+1, TextAnchor.CENTER_LEFT);
        }
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


    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final ProgressChart chart = new ProgressChart();
//                chart.setDimension(new Dimension(300, 50));
                chart.setProgress(71f);
                chart.setProgress(97f);
                chart.setProgressLabel("0.29%");
                chart.setTopLeftLabel("Horticulture");
                chart.setTopRightLabel("2013 target");
                chart.setBottomLeftLabel("0%");
                chart.setBottomRightLabel("80%");
                JFrame frame = new JFrame("Progress Chart Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new JPanel() {
                    {
                        setBorder(BorderFactory.createLineBorder(Color.black));
                    }

                    @Override
                    public Dimension getPreferredSize() {
//                        return chart.getDimension();
                        return new Dimension(300, 100);
                    }

                    @Override
                    public void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Dimension d = chart.getDimension();
                        if(d==null) {
                            try {
                                d=chart.getMinDimension((Graphics2D)g);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                        Graphics2D g2 = (Graphics2D)((Graphics2D)g).create(0, 0, d.width, d.height);
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        chart.draw(g2, new Rectangle(0,0,d.width, d.height));
                        g2.dispose();
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
