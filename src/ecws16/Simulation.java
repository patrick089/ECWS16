package ecws16;

import java.util.ArrayList;
import java.util.Random;

public class Simulation {
    public static final int MAP_WIDTH = 10;
    public static final int MAP_HEIGHT = 10;
    private static final double REQUESTS_SIGMA = 2;
    private static final double REQUESTS_MY = 3;
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
        Request request = generateRequest(currentTime);

       //no retry - migrate random
         if(modus == 1){

            try {
                simulateMigrationRandom();
                setObjectsforMigrationRandom();
                collectAndMigrateObjects();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //no retry - migrate random + SLA
        }else if(modus == 2){

            try {
                simulateMigrationRandomThreeQuarter();
                setObjectsforMigrationRandomThreeQuarter();
                collectAndMigrateObjects();
            } catch (Exception e) {
                e.printStackTrace();
            }

        //retry - migrate random
        }else if (modus == 3){
            /*
            if(request.isDeliverd() == false){
                start retry
            }
            migration
             */
            if (request.isDelivered() == false) {
                while (retry.shouldRetry()) {

                    //request hernehmen sehen wo er gescheitert ist und versuchen nochmal zum zustellen
                    //handlen, dass ob zustellung geglück ist oder nicht
                    //wenn nicht dann siehe *
                    if(request.isDelivered() == false){
                        retry.errorOccured();
                    }


                }
                //* alle 5 versuche aufgebracht andere VM,PM oder edge suchen
            }
             simulateMigrationRandom();
             setObjectsforMigrationRandom();
             collectAndMigrateObjects();

            //retry - migrate random + SLA
        }else if(modus == 4){

            if (request.isDelivered() == false) {
                while (retry.shouldRetry()) {
                    try {

                    } catch (Exception e) {
                        try {
                            retry.errorOccured();
                        } catch (RetryException e1) {
                            e1.printStackTrace();
                        }

                    }
                }
            }
             simulateMigrationRandomThreeQuarter();
             setObjectsforMigrationRandomThreeQuarter();
             collectAndMigrateObjects();
        }

        for (Edge edge : edges) {
            ArrayList<Request> removedRequests = edge.timeStep(currentTime);
            if(removedRequests.size() > 0){
                finishRequests(removedRequests);
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
        int n = (int) Math.round(random.nextGaussian() * REQUESTS_SIGMA + REQUESTS_MY);
        for (int i = 0; i < n; i++) {
            generateRequest(timeStep);
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
                    //System.out.println("dirtyPages: " + vm.getMemory().countDirtyPages() +  ", " + i);
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
        user.getRequest().setDelivered(true);
        return user.getRequest();
    }
    private Edge findNearestEdge(Request request) {
        double distance;
        double minDistance = Double.POSITIVE_INFINITY;
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

    private void collectAndMigrateObjects(){
        for(Edge edge : edges){
            edge.checkIfAllVmsAreAlive();
        }
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
