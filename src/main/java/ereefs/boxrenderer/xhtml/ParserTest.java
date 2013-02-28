package ereefs.boxrenderer.xhtml;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.ui.RefineryUtilities;

import ereefs.boxrenderer.Box;
import ereefs.boxrenderer.xhtml.Parser.Resolver;

public class ParserTest {

    private static final String BURDEKIN_STATUS = "status_burdekin.xml";
    private static final String CAPE_YORK_STATUS ="status_capeyork.xml";

    private static InputStream getStream(String resource) throws Exception  {
        InputStream in = ParserTest.class.getResourceAsStream(resource);
        if(in == null) {
            throw new Exception("failed to load resource " + resource);
        }
        return in;
    }

    public static void main(String[] args) throws Exception {
        Parser parser = new Parser(new Resolver() {
            @Override
            public InputStream resolve(String name) throws Exception {
                return ParserTest.class.getResourceAsStream(name);
            }});
        final Box burdekinBox = parser.parse(getStream(BURDEKIN_STATUS));
        final Box capeyorkBox = parser.parse(getStream(CAPE_YORK_STATUS));
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Box renderer test xhtml");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(new JPanel(){
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(1000, 500);
                    }

                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D)g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        try {
                            Dimension burd = burdekinBox.getDimension(g2);
                            Dimension cape = capeyorkBox.getDimension(g2);
                            burdekinBox.render(g2);
                            Graphics2D g3 = (Graphics2D)g2.create(burd.width+1, 0, cape.width, cape.height);
                            capeyorkBox.render(g3);
                            g3.dispose();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            throw new RuntimeException(e);
                        }
                    }
                });
                frame.pack();
                RefineryUtilities.centerFrameOnScreen(frame);
                frame.setVisible(true);
            }
        });
    }
}
