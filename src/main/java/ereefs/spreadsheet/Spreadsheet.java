package ereefs.spreadsheet;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import ereefs.charts.Chart;

public class Spreadsheet {
    
    private DataSource datasource;

    public Spreadsheet(InputStream in, String mimeType) {
        try {
            datasource = new XlsxDataSource(in);
        } catch(Exception e ) {
            throw new RuntimeException(e);
        }
    }

    public List<Chart> getCharts(Map<String, String[]> properties) {
        List<Chart> result = Lists.newArrayList();
        Marine marine = new Marine(datasource);
        result.addAll(marine.getCharts(properties));
        return result;
    }

}
