package ereefs.charts;

import static ereefs.charts.Configuration.TITLE_FONT;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;

public class SugarcanePracticeChart {

    public static JFreeChart createChart(CategoryDataset dataset) {
        LandPracticeChart chart = new LandPracticeChart();
        chart.setRenderer(new BarLegendRenderer());
        JFreeChart result = chart.createChart(dataset);
        addTitle(result);
        return result;
    }

    private static void addTitle(JFreeChart chart) {
        TextTitle title = new TextTitle("Sugarcane practices", TITLE_FONT);
        chart.addSubtitle(title);
    }
}
