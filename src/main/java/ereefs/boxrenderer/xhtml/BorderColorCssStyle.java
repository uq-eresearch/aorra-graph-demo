package ereefs.boxrenderer.xhtml;

import java.awt.Paint;

import ereefs.boxrenderer.Box;

public class BorderColorCssStyle extends AbstractCssStyle implements CssStyle {

    @Override
    public void style(Box box) {
        Paint paint = Colors.getPaint(this.getProperty().getValue());
        box.getBorder().setPaint(paint);
    }

}
