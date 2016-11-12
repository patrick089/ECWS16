package ecws16;

import java.util.ArrayList;
import java.util.Random;

public class Simulation {
    private static final int MAP_WIDTH = 10;
    private static final int MAP_HEIGHT = 10;
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

    public Simulation(long duration, ArrayList<Edge> edges, int modus) {
        this.duration = duration;
        currentTime = 0;
        this.edges = new ArrayList<>();
        this.edges.addAll(edges);
        requests = new ArrayList<>();
        users = new ArrayList<>();
        userCount = 1;
        random = new Random();
        this.modus = modus;
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

        if(modus == 1){
            //no retry - migrate everytime
            try {
                simulateMigrationRandomThreeQuarter();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                setObjectsforMigrationRandomThreeQuarter();
            } catch (Exception e) {
                e.printStackTrace();
            }
            collectAndMigrateObjects();//TODO Exception

        }else if(modus == 2){
            //no retry - migrate random
            try {
                simulateMigrationRandom();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                setObjectsforMigrationRandom();
            } catch (Exception e) {
                e.printStackTrace();
            }
            collectAndMigrateObjects();//TODO Exception

        }else if(modus == 3){
            //no retry - migrate random + SLA

        }else if(modus == 4){
            //retry - migrate everytime

            if (request.isDelivered() == false) {
                while (retry.shouldRetry()) {
                    try {
                        simulateMigrationRandomThreeQuarter();
                        setObjectsforMigrationRandomThreeQuarter();
                        collectAndMigrateObjects();//TODO Exception
                    } catch (Exception e) {
                        try {
                            retry.errorOccured();
                        } catch (RetryException e1) {
                            e1.printStackTrace();
                        }

                    }
                }
            }

        }else if (modus == 5){
            //retry - migrate random

            if (request.isDelivered() == false) {
                while (retry.shouldRetry()) {
                    try {
                        simulateMigrationRandom();
                        setObjectsforMigrationRandom();
                        collectAndMigrateObjects();//TODO Exception
                    } catch (Exception e) {
                        try {
                            retry.errorOccured();
                        } catch (RetryException e1) {
                            e1.printStackTrace();
                        }

                    }
                }
            }

        }else if(modus == 6){
            //retry - migrate random + SLA


        }

        setObjectsforMigrationRandomThreeQuarter();
        collectAndMigrateObjects();
        /*try {
            setObjectsforMigrationRandomThreeQuarter();
        } catch (Exception e) {
            e.printStackTrace();
        }
        collectAndMigrateObjects();*/

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
        //go throw edges?
        for(Edge edge : edges){
            //no migration for pm's and edges
            //edge.setInMigrationProcess(true);
            for (PM pm : edge.getPms()) {
                //SLA: ensure that there is enough time to migrate
                //if (/*pm.getCapacity() >  (Math.random()*pm.getSize() + 1) ||*/ pm.getCapacity() >= Math.round(pm.getSize() * 0.75)) {
                 //   pm.setInMigrationProcess(true);
                //}
                for (VM vm : pm.getVms()) {
                    //SLA: ensure that there is enough time to migrate
                    if (/*vm.getMemory().countDirtyPages() > (Math.random()*vm.getMemory().getPages().size() + 1) ||*/
                            vm.getMemory().countDirtyPages() >= Math.round((vm.getMemory().getPages().size() * 0.75))) {
                        if (vm.isAlive() == true) {
                            vm.setInMigrationProgress(true);
                        }
                    }
                }
            }
        }

    }


    private void simulateMigrationRandom() throws Exception {
        int n = (int) Math.round(random.nextGaussian() * MIGRATION_SIGMA + MIGRATION_MY);
        for (int i = 0; i < n; i++) {
            setObjectsforMigrationRandom();
        }
    }

    private void setObjectsforMigrationRandom() throws Exception {
        for(Edge edge : edges){
            edge.setInMigrationProcess(true);
            for (PM pm : edge.getPms()) {
                //SLA: ensure that there is enough time to migrate
                if (pm.getCapacity() >  (Math.random()*pm.getSize() + 1)) {
                    pm.setInMigrationProcess(true);
                }
                for (VM vm : pm.getVms()) {
                    //SLA: ensure that there is enough time to migrate
                    if (vm.getMemory().countDirtyPages() > (Math.random()*vm.getMemory().getPages().size() + 1)) {
                        vm.setInMigrationProgress(true);
                    }
                }
            }
        }

    }

    private Request generateRequest(long timeStep) {
        int x = (int)(Math.random()*MAP_WIDTH);
        int y = (int)(Math.random()*MAP_HEIGHT);
        User user = new User(userCount,x,y);
        users.add(user);
        userCount++;
        Edge nearestEdge = findNearestEdge(user.getRequest());
        nearestEdge.handleRequest(user.getRequest());
        user.getRequest().setDelivered(true);
        return user.getRequest();
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
}
