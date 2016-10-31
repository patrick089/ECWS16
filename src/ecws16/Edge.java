package ecws16;

import java.util.ArrayList;

public class Edge {
    /*
    Includes max 10 PMs, but number of running PMs differs due to the work-
load.
– Location of the edge is known.
– The edges are placed in a grid as shown in Figure 1. At most one edge can
be placed at a position in the grid.
– The distance between each two edges is defined as Manhattan distance be-
tween the edges.
– The edges are connected between each other. Network bandwidth is defined
for each connection between two edges.
U = U 0 +
X
U m
(2)
m∈P M
– U is total energy utilization, U 0 is the energy utilization when no PMs are
running, U m is energy utilization of running PM m.
     */
    private static final double U_0 = 1000;
    private static final int MAX_PMS = 10;
    private static final double FAILURE_PROBABILITY = 0.0001;

    private int x;
    private int y;
    private ArrayList<PM> PMs;

    public Edge(int x, int y, int numberOfPMs) {
        this.x = x;
        this.y = y;
        this.PMs = new ArrayList<>();
        for (int i = 0; i < Math.min(numberOfPMs, 10); i++) {
            this.PMs.add(new PM());
        }
    }

    public int distanceTo(Edge edge) {
        return Math.abs(x - edge.x) + Math.abs(y - edge.y);
    }

    public double getEnergyUtilization() {
        double sum = U_0;
        for (int i = 0; i < PMs.size(); i++) {
            sum += PMs.get(i).getEnergyUtilization();
        }
        return sum;
    }

    public String getLocation() {
        return "("+x+","+y+")";
    }

    public void timeStep() {
        if (Math.random() < FAILURE_PROBABILITY) {
            for (PM pm : PMs) {
                pm.restart();
            }
        }
        for (PM pm : PMs) {
            pm.timeStep();
        }
    }
}
