package ereefs.boxrenderer;

import java.awt.Color;

public class Border extends Spacing {

    public Border() {
        super();
    }

    public Border(int size) {
        super(size);
        setPaint(Color.black);
    }

    public Border(int size, Color color) {
        super(size);
        setPaint(color);
    }

    public Border(int top, int left, int bottom, int right) {
        super(top, left, bottom, right);
        setPaint(Color.black);
    }
}
