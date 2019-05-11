package com.felixawpw.indoormaps.navigation;

public class Agent {
    public Path path;
    public PathFinder pathFinder;
    int currentStep;
    int currX, currY;

    public Agent() {

    }

    public Agent(GridMap map, int xStart, int yStart) {
        this.currX = xStart;
        this.currY = yStart;
        this.pathFinder = new PathFinder(map, 500, false);
    }

    public void GoTo(int x, int y) {
        currentStep = 0;
        System.out.println("Read goto");
        path = pathFinder.findPath(currX, currY, x, y);
        if (path != null) {
            System.out.println("Steps = " + path.steps.size());

            for (Object step : path.steps)
                System.out.println(String.format("%s %s", ((Step)step).getX(), ((Step)step).getY()));

            if (path.steps.isEmpty()) {

            }
            else {

            }
        }
        else
            System.err.println("Cannot find path");
    }
}
