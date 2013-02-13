package ereefs.boxrenderer.xhtml;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ereefs.boxrenderer.Box;
import ereefs.boxrenderer.TableCellBox;
import ereefs.boxrenderer.TableCellBox.Align;

public class TextAlignCssStyle extends AbstractCssStyle implements CssStyle {

    private static final Logger logger = LoggerFactory.getLogger(TextAlignCssStyle.class);

    @Override
    public void style(Box box) {
        String align = getProperty().getValue();
        // currently only works with table cells
        if((box instanceof TableCellBox) && !StringUtils.isBlank(align)) {
            TableCellBox tbox = (TableCellBox)box;
            Align a = Align.valueOf(align.toUpperCase());
            if(a != null) {
                tbox.setAlign(a);
            } else {
                logger.warn("unknown align "+align);
            }
        }
    }

}
