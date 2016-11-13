package ecws16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Edge {
    /*
    Includes max 10 pms, but number of running pms differs due to the work-
load.
– Location of the edge is known.
– The edges are placed in a grid as shown in Figure 1. At most one edge can
be placed at a position in the grid.
– The distance between each two edges is defined as Manhattan distance be-
tween the edges.
– The edges are connected between each other. Network bandwidth is defined
for each connection between two edges.
U = U 0 +
X
U m
(2)
m∈P M
– U is total energy utilization, U 0 is the energy utilization when no pms are
running, U m is energy utilization of running PM m.
     */
    private static final double U_0 = 1000;
    private static final int MAX_PMS = 10;
    private static final double FAILURE_PROBABILITY = 0.0001;


    private ID id;
    private Location location;
    private ArrayList<PM> pms;
    private HashMap<Integer,PM> migrationMap;
    private boolean isAlive;
    private boolean inMigrationProcess;
    private long vmig;


    public Edge(int x, int y, int numberOfPMs) {

        this.id = new ID();
        location = new Location(x,y);
        this.pms = new ArrayList<>();
        for (int i = 0; i < Math.min(numberOfPMs, MAX_PMS); i++) {
            this.pms.add(new PM(10,1250));
        }
        migrationMap = new HashMap<>();
        isAlive =true;
        inMigrationProcess = false;
        this.vmig = 0;
    }

    public double distanceTo(Edge edge) {
        return Math.abs(location.getX() - edge.getLocation().getX()) + Math.abs(location.getY() - edge.getLocation().getY());
    }

    public double getDistanceToRequest(Location location){
        return Math.abs(this.location.getX() - location.getX()) + Math.abs(this.location.getY() - location.getY());
    }

    public double getEnergyUtilization() {
        double sum = U_0;
        for (int i = 0; i < pms.size(); i++) {
            sum += pms.get(i).getEnergyUtilization();
        }
        return sum;
    }

    public Location getLocation() {
        return location;
    }

    public ArrayList<Request> timeStep(long t) {
        ArrayList<Request> removedRequests = new ArrayList<>();
        if (Math.random() < FAILURE_PROBABILITY) {
            for (PM pm : pms) {
                pm.restart();
            }
        }
        for (PM pm : pms) {
            removedRequests = pm.timeStep(t);
        }
        return removedRequests;
    }

    public void handleRequest(Request request){

        request.setEdgeId(this.getId().getId());
        int maxFreeCapacity =  Integer.MIN_VALUE;
        int freeCapacity = 0;
        PM selectedPM = null;
        for(PM pm : pms){
            if(pm.isAlive() == true) {
                freeCapacity = pm.getFreeCapacity();
                if (freeCapacity > maxFreeCapacity) {
                    maxFreeCapacity = freeCapacity;
                    selectedPM = pm;
                }
            }
        }
        selectedPM.handleRequest(request);
    }

    public boolean hasFreeCapacity(Request request){
        boolean free = false;

        for (PM pm : pms){
            int freeCapacity = pm.getFreeCapacity();
            if(freeCapacity > request.getSize()){
                free = true;
                break;
            }
        }

        return free;
    }

    //TODO:Migration

    public void checkIfAllVmsAreAlive(){
        ArrayList<VM> migrationVms = new ArrayList<>();
        for(PM pm : this.getPms()){
            ArrayList<VM> migrationVmsFromPM = new ArrayList<>();
            migrationVmsFromPM = pm.checkIfAllVmsAreAlive();
            migrationVms.addAll(migrationVmsFromPM);
        }

        if(migrationVms.size() > 0){
            for(VM migrationVm : migrationVms) {
                boolean firstMigration = true;
                HashMap<Integer, PM> copieOfMigrationMap = new HashMap<>();
                copieOfMigrationMap.putAll(migrationMap);
                if (copieOfMigrationMap.size() > 0) {
                    for (Map.Entry<Integer, PM> entry : copieOfMigrationMap.entrySet()) {
                        if (entry.getKey() == migrationVm.getId().getId()) {
                            firstMigration = false;
                            VM migrationMapVm = getMigrationMapVm(entry.getKey(), entry.getValue());
                            int difference = migrationVm.getMemory().countDirtyPages() - migrationMapVm.getMemory().countDirtyPages();
                            if (difference > 2) {
                                vmig += difference * migrationMapVm.getMemory().getPages().get(0).getSize();
                                System.out.println("second migration: " + migrationVm.toString());
                                doSecondMigration(migrationVm, entry.getValue() /*, copieOfMigrationMap*/);
                            } else {
                                System.out.println("third migration: " + migrationVm.toString());
                                doLastMigration(migrationVm, entry.getValue()/*, copieOfMigrationMap*/);
                            }
                        }
                    }

                }
                if (firstMigration == true) {
                    System.out.println("first migration: " + migrationVm.toString());
                    doFirstMigration(migrationVm);
                }
            }

        }

    }

    private VM getMigrationMapVm(int id, PM pm) {
        VM vm = null;
        if (pm.isAlive() == true) {
            for (VM pmVm : pm.getVms()) {
                    if (pmVm.getId().getId() == id && pmVm.isAlive() == false) {
                        vm = pmVm;
                        break;
                    }
            }

        }
        return vm;
    }

    private void doFirstMigration(VM migrationVm){

        VM newMigrationVm = new VM(migrationVm);
        vmig += newMigrationVm.getSize() - newMigrationVm.getMemory().freeCapacity();
        newMigrationVm.setAlive(false);
        newMigrationVm.setInMigrationProgress(false);
        PM pm = searchForNewPM(newMigrationVm);
        migrationMap.put(newMigrationVm.getId().getId(),pm);

    }

    private PM searchForNewPM(VM migrationVM) {
        PM migrationPm = null;
        for(int i = 0; i < this.getPms().size(); i++){
            boolean samePm = false;
            PM pm = this.getPms().get(i);
            for(VM vm : pm.getVms()){
                if(vm.getId().getId() == migrationVM.getId().getId()){
                    samePm = true;
                    break;
                }
            }
            if(samePm == false) {
                int freeSpace = pm.getSize() - pm.getCapacity();
                if (freeSpace > migrationVM.getSize()) {
                    pm.getVms().add(migrationVM);
                    migrationPm = pm;
                    break;
                }
            }
        }
        return migrationPm;
    }


    private void doSecondMigration(VM migrationVm, PM migrationMapPm){

        VM migrationMapVm = getMigrationMapVm(migrationVm.getId().getId(),migrationMapPm);

        migrationMap.remove(migrationMapVm);
        migrationMapPm.getVms().remove(migrationMapVm);
        migrationMapVm.setMemory(migrationVm.getMemory());
        migrationMapPm.getVms().add(migrationMapVm);
        migrationMap.put(migrationMapVm.getId().getId(),migrationMapPm);

    }

    private void doLastMigration(VM migrationVm , PM migrationMapPm){

        migrationVm.die();
        VM migrationMapVm = getMigrationMapVm(migrationVm.getId().getId(), migrationMapPm);

        migrationMap.remove(migrationMapVm.getId().getId());

        migrationMapPm.getVms().remove(migrationMapVm);
        migrationMapVm.getMemory().manageDirtyPagesForLastMigration();
        migrationMapVm.setAlive(true);
        migrationMapVm.setInMigrationProgress(false);
        migrationMapPm.getVms().add(migrationMapVm);
        System.out.println("Vmig: " + vmig);

    }


    public void die(){
        isAlive = false;
        inMigrationProcess = false;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isInMigrationProcess() {
        return inMigrationProcess;
    }

    public void setInMigrationProcess(boolean inMigrationProcess) {
        this.inMigrationProcess = inMigrationProcess;
    }

    public ArrayList<PM> getPms() {
        return pms;
    }

    public ID getId() {
        return id;
    }

}
