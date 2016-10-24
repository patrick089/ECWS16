package ecws16;

import java.util.ArrayList;

public class Simulation {
    private ArrayList<Edge> edges;

    public Simulation() {
        edges = new ArrayList<>();
        edges.add(new Edge(0,0));
        edges.add(new Edge(0,2));
        edges.add(new Edge(0,4));
        edges.add(new Edge(0,6));
        edges.add(new Edge(1,5));
        edges.add(new Edge(1,8));
        edges.add(new Edge(2,2));
        edges.add(new Edge(2,4));
        edges.add(new Edge(2,6));
        edges.add(new Edge(3,0));
        edges.add(new Edge(4,2));
        edges.add(new Edge(4,4));
        edges.add(new Edge(4,6));
        edges.add(new Edge(4,8));
        edges.add(new Edge(6,0));
        edges.add(new Edge(6,3));
        edges.add(new Edge(7,2));
        edges.add(new Edge(7,4));
        edges.add(new Edge(7,7));
    }

    public void run() {
        // TODO
    }
}
