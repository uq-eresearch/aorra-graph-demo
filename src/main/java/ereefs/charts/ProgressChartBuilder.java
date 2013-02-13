package ereefs.charts;

import java.text.DecimalFormat;

public class ProgressChartBuilder {

    private String tl;
    private String tr;
    private String bl;
    private String br;
    private int max;

    public ProgressChartBuilder(String tl, String tr, int max) {
        this.tl = tl;
        this.tr = tr;
        this.bl = "0%";
        this.br = max+"%";
        this.max = max;
    }

    public ProgressChart get() {
        ProgressChart chart = new ProgressChart();
        chart.setTopLeftLabel(tl);
        chart.setTopRightLabel(tr);
        chart.setBottomLeftLabel(bl);
        chart.setBottomRightLabel(br);
        return chart;
    }

    public ProgressChart get(float progress) {
        ProgressChart chart = get();
        String progressLabel = new DecimalFormat("#").format(progress)+"%";
        chart.setProgressLabel(progressLabel);
        chart.setProgress((float)100/max*progress);
        return chart;
    }

}
