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
        return pImage.getMapData().length;
    }

    public int getMapHeight() {
        return pImage.getMapData()[0].length;
    }

    public int[][] getMap() {
        return pImage.getMapData();
    }

    public void visitGrid(int x, int y) {
        visited[x][y] = true;
    }

    public double getCost(int xStart, int yStart, int xMove, int yMove) {
        if (getMap()[xMove][yMove] == 1)
            return 1;
        else
            return 20;
    }

    public boolean isVisited(int x, int y) {
        return visited[x][y];
    }

    public int getMapStatus(int x, int y) {
        return getMap()[x][y];
    }
}
