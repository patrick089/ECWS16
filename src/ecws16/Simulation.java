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
        users = new ArrayList<>();
        userCount = 1;
    }

    public void run() {
        ArrayList<Request> removedRequests = new ArrayList<>();
        for (long t = 0; t < duration; t++) {
            generateRequests(t);
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

    private void generateRequest(long timeStep) {
        int x = (int)(Math.random()*MAP_WIDTH);
        int y = (int)(Math.random()*MAP_HEIGHT);
        User user = new User(userCount,x,y);
        users.add(user);
        userCount++;
        Edge nearestEdge = findNearestEdge(user.getRequest());
        nearestEdge.handleRequest(user.getRequest());
        //TODO: assign request to vm
    }
    //TODO: find also the Edge with the lowest Memory - Julia
    private Edge findNearestEdge(Request request) {
        int distance;
        int minDistance = Integer.MAX_VALUE;
        Edge edge = null;
        for(int i = 0; i < edges.size(); i++){
            distance = edges.get(i).getDistanceToRequest(request.getLocation());
            if(distance < minDistance){
                minDistance = distance;
                edge = edges.get(i);
            }
        }
        return edge;
    }

}
