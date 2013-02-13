package ereefs.boxrenderer.xhtml;

import org.apache.commons.lang3.StringUtils;

public class Sizes {

    public int getPixelSize(String definition) {
        return Integer.parseInt(StringUtils.removeEnd(definition, "px"));
    }

}
