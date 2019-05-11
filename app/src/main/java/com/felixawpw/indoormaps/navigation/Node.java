package com.felixawpw.indoormaps.navigation;

public class Node implements Comparable{
    int x, y;
    double cost, heuristic;
    Node parent;
    int depth;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int setParent(Node parent) {
        depth = parent.depth + 1;
        this.parent = parent;
        return depth;
    }

    @Override
    public int compareTo(Object other) {
        Node o = (Node) other;
        double fValue = heuristic + cost;
        double ofValue = o.heuristic + o.cost;
        if (fValue < ofValue)
            return -1;
        else if (fValue > ofValue)
            return 1;
        else
            return 0;
    }
}
