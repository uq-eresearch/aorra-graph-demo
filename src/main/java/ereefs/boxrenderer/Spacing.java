package ereefs.boxrenderer;

import java.awt.Graphics2D;
import java.awt.Paint;

public class Spacing {

    private int top = -1;
    private int left = -1;
    private int bottom = -1;
    private int right = -1;

    private int size;

    private Paint paint;

    private boolean render = true;

    private boolean applicable = true;

    public Spacing() {
        this(0);
    }

    public Spacing(int s) {
        size = s;
    }

    public Spacing(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public Graphics2D render(Graphics2D g2) {
        int width = g2.getClipBounds().width;
        int height = g2.getClipBounds().height;
        if(isApplicable()) {
            if(render && (paint !=null)) {
                g2.setPaint(paint);
                if(getTop() > 0) {
                    g2.fillRect(0, 0, width, getTop());
                }
                if(getBottom() > 0) {
                    g2.fillRect(0, height-getBottom(), width, height);
                }
                if(getLeft() > 0) {
                    g2.fillRect(0, 0, getLeft(), height);
                }
                if(getRight() > 0) {
                    g2.fillRect(width-getRight(), 0, width, height);
                }
            }
            return (Graphics2D)g2.create(getLeft(), getTop(), width-getLeft()-getRight(), height-getTop()-getBottom());
        } else {
            return (Graphics2D)g2.create(0, 0, width, height);
        }
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTop() {
        return top!=-1?top:size;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left!=-1?left:size;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getBottom() {
        return bottom!=-1?bottom:size;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getRight() {
        return right!=-1?right:size;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public boolean isApplicable() {
        return applicable;
    }

    public void setApplicable(boolean applicable) {
        this.applicable = applicable;
    }

    public Paint getPaint() {
        return paint;
    }

}
