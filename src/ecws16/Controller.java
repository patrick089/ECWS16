package ecws16;


import java.util.ArrayList;

public class Controller {

    private ArrayList<Edge> edges;
    private Simulation simulation;

    public Controller(int duration, int numberOfEdges, int modus, double failureProbability){
        edges = new ArrayList<>();
        generateEdges(numberOfEdges);
        simulation = new Simulation(duration,edges,modus, failureProbability);
    }

    private void generateEdges(int numberOfEdges) {
        System.out.println("Generating edges...");
        do {
            generateEdge();
        } while (edges.size() < numberOfEdges);
        System.out.println("Edges generated.");
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
        edges.add(new Edge(x,y,10));
    }

    public Simulation getSimulation() {
        return simulation;
    }
}
