package ecws16;

import java.util.ArrayList;

public class Simulation {
    private static final int MAP_WIDTH = 7;
    private static final int MAP_HEIGHT = 7;

    private ArrayList<Edge> edges;
    private ArrayList<Request> requests;
    private long duration;

    public Simulation(long duration) {
        this.duration = duration;
        edges = new ArrayList<>();
        edges.add(new Edge(0,0, 5));
        edges.add(new Edge(0,2, 5));
        edges.add(new Edge(0,4, 5));
        edges.add(new Edge(0,6, 5));
        edges.add(new Edge(1,5, 5));
        edges.add(new Edge(1,8, 5));
        edges.add(new Edge(2,2, 5));
        edges.add(new Edge(2,4, 5));
        edges.add(new Edge(2,6, 5));
        edges.add(new Edge(3,0, 5));
        edges.add(new Edge(4,2, 5));
        edges.add(new Edge(4,4, 5));
        edges.add(new Edge(4,6, 5));
        edges.add(new Edge(4,8, 5));
        edges.add(new Edge(6,0, 5));
        edges.add(new Edge(6,3, 5));
        edges.add(new Edge(7,2, 5));
        edges.add(new Edge(7,4, 5));
        edges.add(new Edge(7,7, 5));
        requests = new ArrayList<>();
    }

    public void run() {
        for (long t = 0; t < duration; t++) {
            generateRequests(t);
            for (Edge edge : edges) {
                edge.timeStep();
            }
        }
    }

    private void generateRequests(long timeStep) {
        if (Math.random() < 0.5) {
            generateRequest();
        }
    }

    private void generateRequest() {
        int x = (int)(Math.random()*MAP_WIDTH);
        int y = (int)(Math.random()*MAP_HEIGHT);
        Request request = new Request(x,y);
        //TODO: assign request to vm
    }
}
