package ereefs.charts;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class CategoryDatasetBuilder {

    private DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    public CategoryDatasetBuilder addValue(double value, Comparable<?> rowKey, Comparable<?> columnKey) {
        dataset.addValue(value, rowKey, columnKey);
        return this;
    }

    public CategoryDataset get() {
        return dataset;
    }

}
