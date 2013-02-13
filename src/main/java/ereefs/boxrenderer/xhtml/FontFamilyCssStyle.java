package ereefs.boxrenderer.xhtml;

import ereefs.boxrenderer.Box;

public class FontFamilyCssStyle extends AbstractCssStyle implements CssStyle {

    @Override
    public void style(Box box) {
        box.setFontFamily(getProperty().getValue());
    }

}
