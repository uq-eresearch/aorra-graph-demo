package ereefs.charts;

import static ereefs.charts.Configuration.TITLE_FONT;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;

public class HorticulturePracticeChart {

    public static JFreeChart createChart(CategoryDataset dataset) {
        LandPracticeChart chart = new LandPracticeChart();
        chart.setRenderer(new BarLegendRenderer());
        JFreeChart result = chart.createChart(dataset);
        addTitle(result);
        return result;
    }

    private static void addTitle(JFreeChart chart) {
        TextTitle title = new TextTitle("Horticulture practices", TITLE_FONT);
        chart.addSubtitle(title);
//        title.setPadding(new RectangleInsets(10,0,0,0));
//        try {
//            Image image = GraphUtils.getImage("images/banana.png");
//            Image scaled = image.getScaledInstance(image.getWidth(null)/2, -1, 0);
//            ImageTitle imageTitle = new ImageTitle(scaled);
//            CompositeTitle cTitle = new CompositeTitle();
//            cTitle.getContainer().setArrangement(new FlowArrangement());
//            cTitle.getContainer().add(title);
//            cTitle.getContainer().add(imageTitle);
//            cTitle.setMargin(new RectangleInsets(0,30,0,0));
//            chart.addSubtitle(cTitle);
//        } catch(IOException e) {
//            throw new RuntimeException(e);
//        }
    }

}
