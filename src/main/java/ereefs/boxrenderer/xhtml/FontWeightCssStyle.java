package ereefs.boxrenderer.xhtml;

import ereefs.boxrenderer.Box;

public class FontWeightCssStyle extends AbstractCssStyle implements CssStyle {

    @Override
    public void style(Box box) {
        box.setBold("bold".equals(getProperty().getValue()));
    }

}
