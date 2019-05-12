package com.felixawpw.indoormaps.navigation;


import android.util.Log;

import com.felixawpw.indoormaps.util.SortedList;

import java.util.HashSet;
import java.util.Set;

public class PathFinder {
    private Set closed = new HashSet();
    private Set openSet = new HashSet();
    private SortedList open = new SortedList();

    private GridMap map;
    private int maxSearchDistance;

    private Node[][] nodes;
    private boolean allowDiagMovement;
    private Heuristic heuristic;

    public double totalCost = 0;

    public PathFinder(GridMap map, int maxSearchDistance, boolean allowDiagMovement) {
        this(map, maxSearchDistance, allowDiagMovement, new Heuristic());
    }

    public PathFinder(GridMap map, int maxSearchDistance, boolean allowDiagMovement, Heuristic heuristic) {
        this.heuristic = heuristic;
        this.map = map;
        this.maxSearchDistance = maxSearchDistance;
        this.allowDiagMovement = allowDiagMovement;

        nodes = new Node[map.getMapWidth()][map.getMapHeight()];
        System.out.println("Map width : height = " + map.getMapWidth() + " : " + map.getMapHeight());
        for (int x = 0; x < map.getMapWidth(); x++)
            for (int y = 0; y < map.getMapHeight(); y++)
                nodes[x][y] = new Node(x,y);
    }

    public Path findPath(int xStart, int yStart, int xGoal, int yGoal)
    {
        closed.clear();
        open.clear();
        openSet.clear();

        nodes[xStart][yStart].cost = 0;
        nodes[xStart][yStart].depth = 0;
        open.add(nodes[xStart][yStart]);
        openSet.add(nodes[xStart][yStart]);

        nodes[xGoal][yGoal].parent = null;
        int iteration = 0;
        while (open.size() != 0)
        {
            Node current = (Node) open.first();
            if (current == nodes[xGoal][yGoal])
                break;

            open.remove(current);
            closed.add(current);
            openSet.remove(current);

            for (int x = -1 ; x <= 1; x++)
            {
                for (int y = -1 ; y <= 1; y++)
                {
                    if ((x == 0) && (y == 0))
                        continue;

                    if (!allowDiagMovement)
                        if ((x != 0) && (y != 0))
                            continue;

                    int xp = x + current.x;
                    int yp = y + current.y;

                    if (isValid(xStart,yStart,xp,yp)) {
                        Node neighbour = nodes[xp][yp];
                        boolean inOpen = openSet.contains(neighbour);
                        boolean inClosed = closed.contains(neighbour);

                        double nextStepCost = current.cost + map.getCost(current.x, current.y, xp, yp);


                        map.visitGrid(xp, yp);

                        if (nextStepCost < neighbour.cost) {
                            if (inOpen) {
                                open.remove(neighbour);
                                openSet.remove(neighbour);
                            }
                            if (inClosed) {
                                closed.remove(neighbour);
                            }
                        }

                        if (!inOpen && !inClosed) {
                            neighbour.cost = nextStepCost;
                            neighbour.heuristic = heuristic.getCost(map, xp, yp, xGoal, yGoal);
                            neighbour.setParent(current);
                            open.add(neighbour);
                            openSet.add(neighbour);
                            totalCost = neighbour.cost;
                        }
                    }
                }
            }
        }
        if (nodes[xStart][yStart].x == nodes[xGoal][yGoal].x &&
                nodes[xStart][yStart].y == nodes[xGoal][yGoal].y)
            return new Path();
        if (nodes[xGoal][yGoal].parent == null) {
            return null;
        }

        Path path = new Path();
        Node target = nodes[xGoal][yGoal];
        while (target != nodes[xStart][yStart]) {
            path.steps.add(0, new Step(target.x, target.y));
            target = target.parent;
        }
        path.steps.add(0, new Step(xStart, yStart));
        return path;
    }

    protected boolean isValid(int xStart, int yStart, int x, int y) {
        boolean invalid = (x < 0) || (y < 0) || (x >= map.getMapWidth()) || (y >= map.getMapHeight());
        return !invalid;
    }

}
