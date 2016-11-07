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
        simulation = new Simulation(5000,edges,modus);
    }

    //to improve: location of edges
    private void generateEdges(int numberOfEdges) {
        for(int i = 0; i < numberOfEdges; i++){
            edges.add(new Edge(i,i,5));
        }
    }

    public Simulation getSimulation() {
        return simulation;
    }
}
