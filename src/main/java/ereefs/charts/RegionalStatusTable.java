package ereefs.charts;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import org.jfree.ui.Drawable;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.google.common.collect.Lists;

import ereefs.boxrenderer.Box;
import ereefs.boxrenderer.xhtml.Parser;
import ereefs.boxrenderer.xhtml.Parser.Resolver;
import ereefs.utils.XmlUtils;

public class RegionalStatusTable implements Drawable {

    public class Cell {
        private String label;
        private Condition condition;
        private boolean hatched;

        public Cell(String label, Condition condition) {
            super();
            this.label = label;
            this.condition = condition;
        }

        public Cell(String label, Condition condition, boolean hatched) {
            this(label, condition);
            this.hatched = hatched;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }

        public boolean isHatched() {
            return hatched;
        }

        public void setHatched(boolean hatched) {
            this.hatched = hatched;
        }
    }

    public static class Catchment {
        private String name;
        private Cell wetland;
        private Cell riparian;

        public Catchment(String name, Cell wetland, Cell riparian) {
            super();
            this.name = name;
            this.wetland = wetland;
            this.riparian = riparian;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Cell getWetland() {
            return wetland;
        }

        public void setWetland(Cell wetland) {
            this.wetland = wetland;
        }

        public Cell getRiparian() {
            return riparian;
        }

        public void setRiparian(Cell riparian) {
            this.riparian = riparian;
        }
    }

    private Cell grazing;
    private Cell horticulture;
    private Cell sugarcane;
    private Cell wetland;
    private Cell riparian;
    private Cell nitrogen;
    private Cell phosphorus;
    private Cell sediment;
    private Cell pesticides;
    private Cell waterQuality;
    private Cell seagrass;
    private Cell coral;

    private List<Catchment> catchments = Lists.newArrayList();

//    private final static String DEBUG = "<table><theme1>#ABA998</theme1><theme2>#676651</theme2>" +
//    		"<catchment><name>Black</name></catchment>" +
//    		"<catchment><name>Burdekin</name></catchment>" +
//    		"<catchment><name>Don</name></catchment>" +
//    		"<catchment><name>Haughton</name></catchment>" +
//    		"<catchment><name>Ross</name></catchment>" +
//    		"<aip>" +
//    		"<Grazing>21</Grazing>" +
//    		"<Hort>21</Hort>" +
//    		"<Sugar>21</Sugar>" +
//    		"</aip>" +
//    		"</table>";

    private static final String STYLESHEET = "status.xslt";

    //private Color theme1;

    //private Color theme2;

/*
    private String grazing;

    private String horticulture;

    private String sugarcane;
*/
    public RegionalStatusTable() {
        
    }

    @Override
    public void draw(Graphics2D g2, Rectangle2D rectangle) {
        try {
            g2.setClip(rectangle);
            getBox(g2).render(g2);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Dimension getMinDimension(Graphics2D g2) throws Exception {
        return getBox(g2).getDimension(g2);
    }

    private Box getBox(Graphics2D g2) throws Exception {
        InputStream stylesheet = getStream(STYLESHEET);
        //Document doc = XmlUtils.newDocument();
        //Document doc = XmlUtils.parse(new InputSource(new StringReader(DEBUG)));
        Document doc = XmlUtils.parse(new InputSource(getStream("test.xml")));
        Document result = XmlUtils.xslt(stylesheet, doc);
        System.out.println(XmlUtils.serialize(result));
        Parser parser = new Parser(new Resolver() {
            @Override
            public InputStream resolve(String name) throws Exception {
                return getStream(name);
            }});
        return parser.parse(result);
    }

    private static InputStream getStream(String resource) throws Exception  {
        InputStream in = RegionalStatusTable.class.getResourceAsStream(resource);
        if(in == null) {
            throw new Exception("failed to load resource " + resource);
        }
        return in;
    }

}

