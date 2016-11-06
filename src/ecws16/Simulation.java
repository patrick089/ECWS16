package ecws16;

import java.util.ArrayList;

public class Simulation {
    private static final int MAP_WIDTH = 7;
    private static final int MAP_HEIGHT = 7;

    private ArrayList<Edge> edges;
    private ArrayList<Request> requests;
    private ArrayList<User> users;
    private long duration;
    private int userCount;

    public Simulation(long duration) {
        this.duration = duration;
        edges = new ArrayList<>();
        edges.add(new Edge(0,0, 10));
        edges.add(new Edge(0,2, 10));
        edges.add(new Edge(0,4, 10));
        edges.add(new Edge(0,6, 10));
        edges.add(new Edge(1,5, 10));
        edges.add(new Edge(1,8, 10));
        edges.add(new Edge(2,2, 10));
        edges.add(new Edge(2,4, 10));
        edges.add(new Edge(2,6, 10));
        edges.add(new Edge(3,0, 10));
        edges.add(new Edge(4,2, 10));
        edges.add(new Edge(4,4, 10));
        edges.add(new Edge(4,6, 10));
        edges.add(new Edge(4,8, 10));
        edges.add(new Edge(6,0, 10));
        edges.add(new Edge(6,3, 10));
        edges.add(new Edge(7,2, 10));
        edges.add(new Edge(7,4, 10));
        edges.add(new Edge(7,7, 10));
        requests = new ArrayList<>();
        users = new ArrayList<>();
        userCount = 1;
    }

    public void run() {
        ArrayList<Request> removedRequests = new ArrayList<>();
        for (long t = 0; t < duration; t++) {
            generateRequests(t);
            simulateMigration();
            checkIfAllVmsAreAlive();
            for (Edge edge : edges) {
                removedRequests = edge.timeStep(t);
                if(removedRequests.size() > 0){
                    finishRequests(removedRequests);
                }
            }
        }
    }

    private void finishRequests(ArrayList<Request> removedRequests) {
        ArrayList<User> newUsers = new ArrayList<>();
        newUsers.addAll(users);
        for(int i = 0; i < removedRequests.size(); i++){
            for(int j = 0; j < users.size(); j++){
                if(removedRequests.get(i).getUserId() == users.get(j).getId()){
                    newUsers.remove(j);
                }
            }
        }
        users = newUsers;
    }

    private void generateRequests(long timeStep) {
        if (Math.random() < 0.5) {
            generateRequest(timeStep);
        }
    }

    private void simulateMigration() {
        if (Math.random() < 0.5) {
            generateToMigrationVM();
        }
    }

    private void generateToMigrationVM() {

        int randomNumber1 =  (int) ((Math.random() * 19));
        Edge edge = edges.get(randomNumber1);
        int randomNumber2 = (int) ((Math.random() * 10));
        PM pm = edge.getPms().get(randomNumber2);
        VM vm = pm.getVms().get(0);
        vm.setInMigrationProgress(true);
    }

    private void generateRequest(long timeStep) {
        int x = (int)(Math.random()*MAP_WIDTH);
        int y = (int)(Math.random()*MAP_HEIGHT);
        User user = new User(userCount,x,y);
        users.add(user);
        userCount++;
        Edge nearestEdge = findNearestEdge(user.getRequest());
        nearestEdge.handleRequest(user.getRequest());
    }
    private Edge findNearestEdge(Request request) {
        int distance;
        int minDistance = Integer.MAX_VALUE;
        Edge edge = null;
        for(int i = 0; i < edges.size(); i++){
            distance = edges.get(i).getDistanceToRequest(request.getLocation());
            if(edges.get(i).hasFreeCapacity(request) == true) {
                if (distance < minDistance) {
                    minDistance = distance;
                    edge = edges.get(i);
                }
            }
        }
        return edge;
    }

    private void checkIfAllVmsAreAlive(){
        for(Edge edge : edges){
            edge.checkIfAllVmsAreAlive();
        }
    }

}
