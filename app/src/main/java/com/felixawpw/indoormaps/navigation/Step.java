package com.felixawpw.indoormaps.navigation;

public class Step {

    private int x;
    private int y;


    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }
    //</editor-fold>

    public Step(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        return getX() * getY();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Step) {
            Step o = (Step) other;
            return (o.getX() == getX() && o.getY() == getY());
        }
        return false;
    }
}
