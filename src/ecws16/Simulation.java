package ecws16;

import java.util.ArrayList;
import java.util.Random;

public class Simulation {
    public static final int MAP_WIDTH = 50;
    public static final int MAP_HEIGHT = 50;
    public static final double REQUESTS_MY = 30;
    private static final double REQUESTS_SIGMA = REQUESTS_MY/2;
    private static final double MIGRATION_SIGMA = 0.5;
    private static final double MIGRATION_MY = 0.5;

    private ArrayList<Edge> edges;
    private ArrayList<Request> requests;
    private ArrayList<User> users;
    private long duration;
    private long currentTime;
    private int userCount;
    private Random random;
    private int modus;
    private double failureProbability;

    public Simulation(long duration, ArrayList<Edge> edges, int modus, double failureProbability) {
        this.duration = duration;
        currentTime = 0;
        this.edges = new ArrayList<>();
        this.edges.addAll(edges);
        requests = new ArrayList<>();
        users = new ArrayList<>();
        userCount = 1;
        random = new Random();
        this.modus = modus;
        this.failureProbability = failureProbability;
    }

    public void run() throws Exception {
        for (currentTime = 0; !simulationIsOver();) {
            simulateTimestep();
        }
    }

    public boolean simulationIsOver() {
        return currentTime >= duration;
    }

    public void simulateTimestep() throws Exception {
        currentTime++;
        generateRequests(currentTime);
        RetryStrategy retry = new RetryStrategy();
        //Request request = generateRequest(currentTime);

        for(Request request : requests) {
            //no retry - migrate random
            if (modus == 1) {

                try {
                    simulateMigrationRandom();
                    setObjectsforMigrationRandom();
                    collectAndMigrateObjects();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //no retry - migrate random + SLA
            } else if (modus == 2) {

                try {
                    simulateMigrationRandomThreeQuarter();
                    setObjectsforMigrationRandomThreeQuarter();
                    collectAndMigrateObjects();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (modus == 3 || modus == 4) {
                System.out.println("request check if it is delvierd: " + request.isDelivered());
                Request newRequest = null;
                if (request.isDelivered() == false) {
                    boolean success = false;
                    while (retry.shouldRetry()) {
                        for (Edge edge : edges) {
                            if (edge.getId().getId() == request.getEdgeId()) {
                                if (request.getPmId() != 0) {
                                    for (PM pm : edge.getPms()) {
                                        if (pm.getId().getId() == request.getPmId()) {
                                            if (request.getVmId() != 0) {
                                                for (VM vm : pm.getVms()) {
                                                    if (vm.getId().getId() == request.getVmId()) {
                                                        System.out.println("request modus fails on VM");
                                                        success = sendRequestAgainFailsOnVm(request, vm);
                                                        break;
                                                    }
                                                }
                                            } else {
                                                System.out.println("request modus fails on PM");
                                                success = sendRequestAgainFailsOnPm(request, pm);
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    System.out.println("request modus fails on edge");
                                    success = sendRequestAgainFailsOnEdge(request, edge);
                                    break;
                                }
                            }
                            if (success == true) {
                                System.out.println("Success!!");
                                break;
                            }
                        }

                        if (request.isDelivered() == false) {
                            System.out.println("no sucess!");
                            retry.errorOccured();
                        }else {
                            break;
                        }

                    }
                    if (success == false) {
                        System.out.println("No success 5 times.");
                        newRequest = sendRequestAgain(currentTime, request);
                        System.out.println("new request: " + newRequest.toString());
                        request = newRequest;
                    }

                }
                if(modus == 3){
                    simulateMigrationRandom();
                    setObjectsforMigrationRandom();
                    collectAndMigrateObjects();

                }else {
                    simulateMigrationRandomThreeQuarter();
                    setObjectsforMigrationRandomThreeQuarter();
                    collectAndMigrateObjects();
                }

            }
        }
        if(requests.size() > 0){
            finishRequests();
        }
    }

    private boolean sendRequestAgainFailsOnVm(Request request, VM vm) {

        vm.handleRequest(request,modus,failureProbability);
        return checkIfRequestIsDeliverd(request);

    }

    private boolean sendRequestAgainFailsOnPm(Request request, PM pm) {

        pm.handleRequest(request,modus,failureProbability);
        return checkIfRequestIsDeliverd(request);

    }

    private boolean sendRequestAgainFailsOnEdge(Request request, Edge edge) {
        edge.handleRequest(request,modus,failureProbability);
        return checkIfRequestIsDeliverd(request);
    }

    private boolean checkIfRequestIsDeliverd(Request request){
        if(request.isDelivered() == true){
            return true;
        }else {
            return false;
        }
    }

    private void finishRequests() {
        ArrayList<Request> newRequests = new ArrayList<>();
        newRequests.addAll(requests);
        for(int i = 0; i < requests.size(); i++){
            if(requests.get(i).isDelivered() == true){
                newRequests.remove(requests.get(i));
            }
        }
        requests = newRequests;
    }

    private void generateRequests(long timeStep) {
        int n = (int) Math.round(random.nextGaussian() * REQUESTS_SIGMA + REQUESTS_MY);
        for (int i = 0; i < n; i++) {
            Request request = generateRequest(timeStep);
            requests.add(request);
        }
    }

    private void simulateMigrationRandomThreeQuarter() throws Exception {
        int n = (int) Math.round(random.nextGaussian() * MIGRATION_SIGMA + MIGRATION_MY);
        for (int i = 0; i < n; i++) {
            setObjectsforMigrationRandomThreeQuarter();
        }
    }

    private void setObjectsforMigrationRandomThreeQuarter() throws Exception {
        for(Edge edge : edges){
            for (PM pm : edge.getPms()) {
                for (VM vm : pm.getVms()) {
                    //SLA: ensure that there is enough time to migrate
                    int i = (int) (Math.random()*vm.getMemory().getPages().size() + 1);
                    if (vm.getMemory().countDirtyPages() >= i || vm.getMemory().countDirtyPages() > vm.getMemory().getPages().size() * 0.75) {
                        if (vm.isAlive() == true) {
                            vm.setInMigrationProgress(true);
                        }
                    }
                }
            }
        }

    }

    private void simulateMigrationRandom() throws Exception {
        int n = (int) Math.round(currentTime * MIGRATION_SIGMA + MIGRATION_MY);
        for (int i = 0; i < n; i++) {
            setObjectsforMigrationRandom();
        }
    }

    private void setObjectsforMigrationRandom() throws Exception {
        for(Edge edge : edges){
            for (PM pm : edge.getPms()) {
                for (VM vm : pm.getVms()) {
                    //SLA: ensure that there is enough time to migrate
                    int i = (int) (Math.random()*vm.getMemory().getPages().size() + 1);
                    if (vm.getMemory().countDirtyPages() > i) {
                        vm.setInMigrationProgress(true);
                    }
                }
            }
        }

    }

    private Request generateRequest(long timeStep) {
        double x = (Math.random()*MAP_WIDTH);
        double y = (Math.random()*MAP_HEIGHT);
        User user = new User(userCount,x,y, timeStep);
        users.add(user);
        userCount++;
        Edge nearestEdge = findNearestEdge(user.getRequest());
        nearestEdge.handleRequest(user.getRequest(), modus, failureProbability);
        return user.getRequest();
    }
    private Edge findNearestEdge(Request request) {
        double distance;
        double minDistance = Double.POSITIVE_INFINITY;
        Edge edge = null;
        for(int i = 0; i < edges.size(); i++){
            if (edges.get(i).getId().getId() != request.getEdgeId()) {
                distance = edges.get(i).getLocation().distanceTo(request.getLocation());
                if (edges.get(i).hasFreeCapacity(request) == true) {
                    if (distance < minDistance) {
                        minDistance = distance;
                        edge = edges.get(i);
                    }
                }
            }
        }
        return edge;
    }

    private void collectAndMigrateObjects(){
        for(Edge edge : edges){
            edge.checkIfAllVmsAreAlive();
        }
    }

    private Request sendRequestAgain(long timestep, Request request){
        User currentUser = null;
        for(User user : users){
            if(user.getId() == request.getUserId()){
                currentUser = user;
                break;
            }
        }
        request.setEdgeId(0);
        request.setPmId(0);
        request.setVmId(0);
        currentUser.setRequest(request);
        Edge nearestEdge = findNearestEdge(currentUser.getRequest());
        nearestEdge.handleRequest(currentUser.getRequest(), modus, failureProbability);
        currentUser.getRequest().setDelivered(true);
        return currentUser.getRequest();
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public long getDuration() {
        return duration;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public static int getMapWidth() {
        return MAP_WIDTH;
    }

    public static int getMapHeight() {
        return MAP_HEIGHT;
    }

    public int getModus() {
        return modus;
    }
}
