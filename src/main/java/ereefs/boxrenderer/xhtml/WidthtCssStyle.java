package ereefs.boxrenderer.xhtml;

import org.apache.commons.lang3.StringUtils;

import ereefs.boxrenderer.Box;

public class WidthtCssStyle extends AbstractCssStyle implements CssStyle {

    @Override
    public void style(Box box) {
        box.setWidth(Integer.parseInt(StringUtils.removeEnd(getProperty().getValue(), "px")));
    }

}
