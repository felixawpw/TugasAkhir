package com.felixawpw.indoormaps.navigation;

public class GridMap {
    ProcessedImage pImage;
    ImageCustom imageData;

    boolean[][] visited;

    public GridMap() {

    }

    public GridMap(ProcessedImage pImage, ImageCustom imageData) {
        this.pImage = pImage;
        this.imageData = imageData;

        this.visited = new boolean[getMapWidth()][getMapHeight()];
    }

    public int getMapWidth() {
        return imageData.getWidth();
    }

    public int getMapHeight() {
        return imageData.getHeight();
    }

    public int[][] getMap() {
        return pImage.getNormalizedMap();
    }

    public void visitGrid(int x, int y) {
        visited[x][y] = true;
    }

    public double getCost(int xStart, int yStart, int xMove, int yMove) {
        return 1;
    }

    public boolean isVisited(int x, int y) {
        return visited[x][y];
    }

    public int getMapStatus(int x, int y) {
        return getMap()[x][y];
    }

    public boolean blocked(int x, int y) {
        return getMap()[x][y] != ProcessedImage.MAP_ACCESSABLE;
    }
}
