package ecws16;


import java.util.ArrayList;

public class Controller {

    private int numberOfEdges;
    private ArrayList<Edge> edges;
    private int modus;
    private Simulation simulation;
    private double failureProbability;

    public Controller(int duration, int numberOfEdges, int modus, double failureProbability){
        this.numberOfEdges = numberOfEdges;
        edges = new ArrayList<>();
        generateEdges(numberOfEdges);
        this.modus = modus;
        this.failureProbability = failureProbability;
        simulation = new Simulation(duration,edges,modus, failureProbability);
    }

    private void generateEdges(int numberOfEdges) {
        do {
            generateEdge();
        } while (edges.size() < numberOfEdges);
    }

    private void generateEdge() {
        boolean foundLocation;
        int x,y;
        do {
            foundLocation = true;
            x = (int)Math.round(Math.random()*Simulation.MAP_WIDTH);
            y = (int)Math.round(Math.random()*Simulation.MAP_HEIGHT);
            for (int i = 0; i < i; i++) {
                if (edges.get(i).getLocation().equals(new Location(x,y))) {
                    foundLocation = false;
                }
            }
        } while (!foundLocation);
        edges.add(new Edge(x,y,5));
    }

    public Simulation getSimulation() {
        return simulation;
    }
}
