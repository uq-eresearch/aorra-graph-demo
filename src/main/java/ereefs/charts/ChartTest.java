package ereefs.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.ui.RefineryUtilities;

import ereefs.boxrenderer.TableBox;
import ereefs.boxrenderer.TableCellBox;
import ereefs.boxrenderer.TableRowBox;

public abstract class ChartTest {

    protected abstract TableCellBox buildChartBox();

    protected abstract String getTitle();

    private TableBox buildTable() {
        TableBox table = new TableBox();
        TableRowBox row = new TableRowBox();
        row.addCell(buildChartBox());
        table.addRow(row);
        return table;
    }

    public void run() {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                final TableBox table = buildTable();
                table.getMargin().setSize(5);
                JFrame frame = new JFrame(getTitle());
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
