package ecws16;

import java.util.ArrayList;

public class Edge {
    private static final double U_0 = 1000;

    private int x;
    private int y;
    private ArrayList<PM> PMs;

    public Edge(int x, int y) {
        this.x = x;
        this.y = y;
        this.PMs = new ArrayList<>();
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
}
