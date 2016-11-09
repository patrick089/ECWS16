package ecws16;


import java.util.ArrayList;

public class Controller {

    private int numberOfEdges;
    private ArrayList<Edge> edges;
    private int modus;
    private Simulation simulation;

    public Controller(int numberOfEdges, int modus){
        this.numberOfEdges = numberOfEdges;
        edges = new ArrayList<>();
        generateEdges(numberOfEdges);
        this.modus = modus;
        simulation = new Simulation(20,edges,modus);
    }

    private void generateEdges(int numberOfEdges) {
        for(int i = 0; i < numberOfEdges; i++){
            edges.add(new Edge(i,(2+i*7) % numberOfEdges,5));
        }
    }

    public Simulation getSimulation() {
        return simulation;
    }
}
