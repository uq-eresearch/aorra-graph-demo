package ereefs.boxrenderer.xhtml;

import com.osbcp.cssparser.PropertyValue;

import ereefs.boxrenderer.Box;

public abstract class AbstractCssStyle implements CssStyle {

    private PropertyValue property;

    @Override
    public abstract void style(Box box) throws Exception;

    public PropertyValue getProperty() {
        return property;
    }

    public void setProperty(PropertyValue property) {
        this.property = property;
    }

}
