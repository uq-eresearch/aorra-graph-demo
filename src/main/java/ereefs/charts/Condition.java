package ereefs.charts;

import java.awt.Color;

public enum Condition {

    VERY_GOOD("Very good", new Color(0,118,70), Color.white),
    GOOD("Good", new Color(168,198,162), Color.white),
    MODERATE("Moderate", new Color(252,203,38), Color.black),
    POOR("Poor", new Color(244,141,64), Color.black),
    VERY_POOR("Very poor", new Color(233,44,48), Color.black);

    private String label;
    private final Color color;
    private final Color fontColor;

    Condition(String label, Color color, Color fontColor) {
        this.label = label;
        this.color = color;
        this.fontColor = fontColor;
    }

    public String getLabel() {
        return label;
    }

    public Color getColor() {
        return color;
    }

    public Color getFontColor() {
        return fontColor;
    }

}
