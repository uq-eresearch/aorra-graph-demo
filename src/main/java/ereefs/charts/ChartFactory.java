package ereefs.charts;

import java.awt.Dimension;

import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;

public class ChartFactory {

    public static Dimensions getGrazingPracticeChart(Dimension dimension, CategoryDataset dataset) {
        JFreeChart chart = GrazingPracticesChart.createChart(dataset);
        return new DimensionsWrapper(chart, dimension);
    }

    public static Dimensions getHorticulturePracticeChart(Dimension dimension, CategoryDataset dataset) {
        JFreeChart chart = HorticulturePracticeChart.createChart(dataset);
        return new DimensionsWrapper(chart, dimension);
    }

    public static Dimensions getSugarcanePracticeChart(Dimension dimension, CategoryDataset dataset) {
        JFreeChart chart = SugarcanePracticeChart.createChart(dataset);
        return new DimensionsWrapper(chart, dimension);
    }

}
