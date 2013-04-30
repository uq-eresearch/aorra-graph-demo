package ereefs.spreadsheet;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import ereefs.charts.BeerCoaster;
import ereefs.charts.Chart;
import ereefs.charts.ChartDescription;
import ereefs.charts.ChartType;

public class Marine {

    private static final ImmutableMap<String, Integer> OFFSETS =
            new ImmutableMap.Builder<String, Integer>()
                .put("Cape York", 0)
                .put("Wet Tropics", 1)
                .put("Burdekin", 2)
                .put("Mackay Whitsundays", 3)
                .put("Fitzroy", 4)
                .put("Burnett Mary", 5)
                .put("GBR", 6)
                .build();

    private DataSource datasource;

    public Marine(DataSource datasource) {
        this.datasource = datasource;
    }

    public List<Chart> getCharts(Map<String, String[]> properties) {
        List<Chart> result = Lists.newArrayList();
        if(isMarineSpreadsheet()) {
            String region = getRegion(properties);
            if(region != null) {
                Chart chart = getChart(region);
                if(chart != null) {
                    result.add(chart);
                }
            } else {
                for(String r : OFFSETS.keySet()) {
                    Chart chart = getChart(r);
                    if(chart != null) {
                        result.add(chart);
                    }
                }
            }
        }
        return result;
    }

    public boolean isMarineSpreadsheet() {
        try {
            return "MARINE SUMMARY".equalsIgnoreCase(datasource.select("Summary!B18").format("value"));
        } catch(Exception e) {
            return false;
        }
    }

    private Chart getChart(String region) {
        BeerCoaster beercoaster = getDrawable(region);
        if(beercoaster != null) {
            return new Chart(new ChartDescription(
                    ChartType.MARINE, ImmutableMap.of("region", region)), beercoaster);
        } else {
            return null;
        }
    }

    private String getRegion(Map<String, String[]> properties) {
        String[] regions = properties.get("region");
        if(regions!=null && (regions.length > 0)) {
            return regions[0];
        } else {
            return null;
        }
    }

    private BeerCoaster getDrawable(String region) {
        try {
            Integer offset = OFFSETS.get(region);
            if(offset == null) {
                throw new Exception("unknown region "+region);
            }
            Double wa = getValue(datasource, "E", 9, offset);
            Double coral = getValue(datasource, "P", 9, offset);
            Double seag = getValue(datasource, "J", 9, offset);
            Double chla = getValue(datasource, "C", 9, offset);
            Double tss = getValue(datasource, "D", 9, offset);
            Double cs = getValue(datasource, "M", 9, offset);
            Double juv = getValue(datasource, "O", 9, offset);
            Double alg = getValue(datasource, "N", 9, offset);
            Double cover = getValue(datasource, "L", 9, offset);
            Double abu = getValue(datasource, "G", 9, offset);
            Double rep = getValue(datasource, "H", 9, offset);
            Double nut = getValue(datasource, "I", 9, offset);
            Double mc = getValue(datasource, "F", 20, offset);
            BeerCoaster bc = configureInternal(
                wa, coral, seag, chla, tss, cs, juv, alg, cover, abu, rep, nut, mc);
            return bc;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BeerCoaster.Condition determineCondition(Double index) {
        if(index == null) {
            return null;
        } else if(index >= 80) {
            return BeerCoaster.Condition.VERY_GOOD;
        } else if(index >= 60) {
            return BeerCoaster.Condition.GOOD;
        } else if(index >= 40) {
            return BeerCoaster.Condition.MODERATE;
        } else if(index >= 20) {
            return BeerCoaster.Condition.POOR;
        } else {
            return BeerCoaster.Condition.VERY_POOR;
        }
    }

    private BeerCoaster configureInternal(Double wa, Double coral, Double seag, Double chla, 
            Double tss, Double cs, Double juv, Double alg, Double cover, Double abu, 
            Double rep, Double nut, Double mc) {
        BeerCoaster chart = new BeerCoaster();
        chart.setCondition(BeerCoaster.Category.WATER_QUALITY, determineCondition(wa));
        chart.setCondition(BeerCoaster.Category.CORAL, determineCondition(coral));
        chart.setCondition(BeerCoaster.Category.SEAGRASS, determineCondition(seag));
        chart.setCondition(BeerCoaster.Indicator.CHLOROPHYLL_A, determineCondition(chla));
        chart.setCondition(BeerCoaster.Indicator.TOTAL_SUSPENDED_SOLIDS, determineCondition(tss));
        chart.setCondition(BeerCoaster.Indicator.SETTLEMENT, determineCondition(cs));
        chart.setCondition(BeerCoaster.Indicator.JUVENILE, determineCondition(juv));
        chart.setCondition(BeerCoaster.Indicator.ALGAE, determineCondition(alg));
        chart.setCondition(BeerCoaster.Indicator.COVER, determineCondition(cover));
        chart.setCondition(BeerCoaster.Indicator.ABUNDANCE, determineCondition(abu));
        chart.setCondition(BeerCoaster.Indicator.REPRODUCTION, determineCondition(rep));
        chart.setCondition(BeerCoaster.Indicator.NUTRIENT_STATUS, determineCondition(nut));
        chart.setOverallCondition(determineCondition(mc));
        return chart;
    }
    
    private Double getValue(DataSource ds, String column, int row, int rowOffset) throws Exception {
        String str = ds.select("Summary!"+column+(row+rowOffset)).format("value");
        try {
            return Double.parseDouble(str);
        } catch(NumberFormatException e) {
            return null;
        }
    }

}
