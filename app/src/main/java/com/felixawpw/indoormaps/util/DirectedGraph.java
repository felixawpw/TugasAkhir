package com.felixawpw.indoormaps.util;

import com.felixawpw.indoormaps.mirror.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class DirectedGraph {
    int v;
    public static final String TAG = DirectedGraph.class.getSimpleName();
    public List<MarkerNode> nodes;
    Map<Integer, List<MarkerNode>> map = new HashMap<>();

    public void addEdge(MarkerNode n) {
        if (map.containsKey(n.getMark().getMapId())) {
            List<MarkerNode> mNodes = map.get(n.getMark().getMapId());
            mNodes.add(n);
            map.put(n.getMark().getMapId(), mNodes);
        } else {
            List<MarkerNode> mNodes = new ArrayList<>();
            mNodes.add(n);
            map.put(n.getMark().getMapId(), mNodes);
        }
    }

    public DirectedGraph() {
        //1,2
        //1,3
        //2,5
        //3,4
        //5,3


        //1 -> 1,2 ; 1,3
        //2 -> 2,5
        //3 -> 3,4
        //5 -> 5,3
    }

    public MarkerNode bfs(MarkerNode start, Integer goal) {
        Queue<MarkerNode> s = new LinkedList();
        Set<MarkerNode> visited = new HashSet();
        s.add(start);
        visited.add(start);

        while(!s.isEmpty()) {
            MarkerNode v = s.poll();
            if (v.getTargetedMapId() == goal)
                return v;

            for (MarkerNode w : map.get(v.getTargetedMapId())) {
                if (!visited.contains(w)) {
                    visited.add(w);
                    w.setParent(v);
                    s.add(w);
                }
            }
        }
        return null;
    }
}
