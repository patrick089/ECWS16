package ecws16;

import java.util.ArrayList;
import java.util.HashMap;

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

    private Location location;
    private ArrayList<PM> pms;
    private HashMap<VM,PM> migrationMap;


    public Edge(int x, int y, int numberOfPMs) {

        location = new Location(x,y);
        this.pms = new ArrayList<>();
        for (int i = 0; i < Math.min(numberOfPMs, MAX_PMS); i++) {
            this.pms.add(new PM(10,1250));
        }
        migrationMap = new HashMap<>();
    }

    public int distanceTo(Edge edge) {
        return Math.abs(location.getX() - edge.getLocation().getX()) + Math.abs(location.getY() - edge.getLocation().getY());
    }

    public int getDistanceToRequest(Location location){
        return Math.abs(location.getX() - location.getX()) + Math.abs(location.getY() - location.getY());
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

        int minWorkload =  Integer.MAX_VALUE;
        int workload = 0;
        PM selectedPM = null;
        for(PM pm : pms){
            workload = pm.getCapacity();
            if (workload < minWorkload){
                minWorkload = workload;
                selectedPM = pm;
            }
        }
        selectedPM.handleRequest(request);
    }

    public boolean hasFreeCapacity(Request request){
        boolean free = false;

        for (PM pm : pms){
            int freeCapacity = pm.getSize() - pm.getCapacity();
            if(freeCapacity > request.getSize()){
                free = true;
                break;
            }
        }

        return free;
    }

    //TODO:Migration

    public void checkIfAllVmsAreAlive(){
        ArrayList<VM> deadVMs = new ArrayList<>();
        for(PM pm : pms){
            deadVMs = pm.checkIfAllVmsAreAlive();
        }
        if(deadVMs.size() > 0){
            for(int i = 0; i < deadVMs.size(); i++) {
                boolean firstmigration = true;
                if (migrationMap.size() > 0) {
                    for (VM vm : migrationMap.keySet()) {
                        if (vm.getId().getId() == deadVMs.get(i).getId().getId()) {
                            if (vm.getMemory().countDirtyPages() > 3) {
                                firstmigration = false;
                                System.out.println("Secondmigration: " + vm.toString());
                                doMigrationSecondStep(vm, deadVMs.get(i));
                            } else {
                                firstmigration = false;
                                System.out.println("Thirdmigration: " + vm.toString());
                                doMigrationThirdStep(vm, deadVMs.get(i));
                            }
                        }
                    }
                }
                if(firstmigration == true){
                    System.out.println("Firstmigration: " + deadVMs.get(i).toString());
                    doMigrationFirstStep(deadVMs.get(i));
                }

            }
        }
    }

    private void doMigrationFirstStep(VM vm){
        int sizeOfVm = vm.getMemory().getSize();
        for(PM pm : pms){
            int freeSpace = pm. getSize() - pm.getCapacity();
            if(freeSpace >= sizeOfVm){
                ArrayList<VM> newVms = new ArrayList<>();
                newVms.addAll(pm.getVms());
                vm.setAlive(false);
                vm.setInMigrationProgress(false);
                newVms.add(vm);
                pm.setVms(newVms);
                migrationMap.put(vm,pm);
                break;
            }
        }
    }

    private void doMigrationSecondStep(VM vm, VM deadVm) {

        VM newVm = new VM(vm);
        newVm.setMemory(new Memory(deadVm.getMemory()));
        PM pm = migrationMap.get(vm);
        pm.getVms().remove(vm);
        pm.getVms().add(newVm);
        migrationMap.remove(vm);
        migrationMap.put(newVm,pm);

    }

    private void doMigrationThirdStep(VM vm, VM deadVm) {

        deadVm.die();
        migrationMap.remove(vm);
        vm.setAlive(true);

        /*for(PM pm : pms){
            for(int i = 0; i < pm.getVms().size(); i++){
                if (deadVm.equals(pm.getVms().get(i))){
                    pm.getVms().remove(i--);
                    System.out.println("AFTER: PM with dead VM: " + pm.toString());
                }
            }

        }*/

    }

    public ArrayList<PM> getPms() {
        return pms;
    }
}
