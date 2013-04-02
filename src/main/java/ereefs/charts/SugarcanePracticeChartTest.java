package ereefs.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.DefaultExtensionHandler;
import org.apache.batik.svggen.ImageHandlerBase64Encoder;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import ereefs.boxrenderer.ImageRenderBox;
import ereefs.boxrenderer.ImageRenderer;
import ereefs.boxrenderer.TableBox;
import ereefs.boxrenderer.TableCellBox;
import ereefs.boxrenderer.TableRowBox;

public class SugarcanePracticeChartTest {

    private static CategoryDataset createLandPracticeDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String nutrients = "Nutrients";
        dataset.addValue(21.0, "A_08", nutrients);
        dataset.addValue(19.0, "B_08", nutrients);
        dataset.addValue(37.0, "C_08", nutrients);
        dataset.addValue(23.0, "D_08", nutrients);
        dataset.addValue(21.0, "A_09", nutrients);
        dataset.addValue(28.0, "B_09", nutrients);
        dataset.addValue(32.0, "C_09", nutrients);
        dataset.addValue(19.0, "D_09", nutrients);

        String herbicides = "Herbicides";
        dataset.addValue(28.0, "A_08", herbicides);
        dataset.addValue(50.0, "B_08", herbicides);
        dataset.addValue(16.0, "C_08", herbicides);
        dataset.addValue( 6.0, "D_08", herbicides);
        dataset.addValue(23.0, "A_09", herbicides);
        dataset.addValue(53.0, "B_09", herbicides);
        dataset.addValue(20.0, "C_09", herbicides);
        dataset.addValue( 4.0, "D_09", herbicides);

        String soil = "Soil";
        dataset.addValue(30.0, "A_08", soil);
        dataset.addValue(40.0, "B_08", soil);
        dataset.addValue(19.0, "C_08", soil);
        dataset.addValue(11.0, "D_08", soil);
        dataset.addValue(27.0, "A_09", soil);
        dataset.addValue(35.0, "B_09", soil);
        dataset.addValue(29.0, "C_09", soil);
        dataset.addValue(09.0, "D_09", soil);

        return dataset;
    }

    private static TableCellBox buildChartBox() {
        final JFreeChart chart = SugarcanePracticeChart.createChart(createLandPracticeDataset());
        final DimensionsWrapper wrapper = new DimensionsWrapper(chart, new Dimension(500, 500)); 
        TableCellBox chartBox = new TableCellBox(new ImageRenderBox(new ImageRenderer() {
            @Override
            public Dimension getDimension(Graphics2D g2) throws Exception {
                return wrapper.getDimension();
            }

            @Override
            public void render(Graphics2D g2) throws Exception {
                Dimension d = getDimension(g2);
                Rectangle2D.Double rect = new Rectangle2D.Double(0,0, d.width, d.height);
                chart.draw(g2, rect);
            }}));
        return chartBox;
    }

    private static TableBox buildTable() {
        TableBox table = new TableBox();
        TableRowBox row = new TableRowBox();
        row.addCell(buildChartBox());
        table.addRow(row);
        return table;
    }

    public static void main(String[] args) throws Exception {
        TableBox table = buildTable();
        table.getMargin().setSize(5);
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        Document doc = impl.createDocument(svgNS, "svg", null);
        SVGGraphics2D g2 = new SVGGraphics2D(doc,
                new ImageHandlerBase64Encoder(), new DefaultExtensionHandler(), true);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //FIXME
        g2.setClip(0, 0, 1000, 1000);
        table.render(g2);
        Writer out = new OutputStreamWriter(new FileOutputStream(new File("sugarcane.svg")), "UTF-8");
        g2.stream(out, false);
        g2.dispose();

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                final TableBox table = buildTable();
                table.getMargin().setSize(5);
                JFrame frame = new JFrame("Sugarcane Practice Chart Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new JPanel() {
                    {
                        setBorder(BorderFactory.createLineBorder(Color.black));
                    }

                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(600, 600);
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
