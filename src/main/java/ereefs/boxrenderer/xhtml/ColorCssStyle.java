package ereefs.boxrenderer.xhtml;

import java.awt.Paint;

import ereefs.boxrenderer.Box;

public class ColorCssStyle extends AbstractCssStyle implements CssStyle {

    @Override
    public void style(Box box) {
        Paint p = Colors.getPaint(getProperty().getValue());
        box.setColor(p);
    }

}
