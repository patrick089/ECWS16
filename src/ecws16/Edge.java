package ecws16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Edge {
    private static final double U_0 = 1000;
    private static final int MAX_PMS = 10;


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
            this.pms.add(new PM(1000,1250));
        }
        migrationMap = new HashMap<>();
        isAlive =true;
        inMigrationProcess = false;
        this.vmig = 0;
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

    /*public ArrayList<Request> timeStep(long t) {
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
    }*/

    public void handleRequest(Request request, int modus, double failureProbability){

        request.setEdgeId(this.getId().getId());
        boolean failure = false;
        if(modus == 3 || modus == 4){
            failure = Math.random() < failureProbability;
            if(failure == true){
                request.setDelivered(false);
            }else {
                request.setDelivered(true);
            }
        }
        if(failure == false) {
            int maxFreeCapacity = Integer.MIN_VALUE;
            int freeCapacity = 0;
            PM selectedPM = null;
            for (PM pm : pms) {
                if (pm.isAlive() == true) {
                    freeCapacity = pm.getFreeCapacity();
                    if (freeCapacity > maxFreeCapacity) {
                        maxFreeCapacity = freeCapacity;
                        selectedPM = pm;
                    }
                }
            }
            selectedPM.handleRequest(request, modus, failureProbability);
        }
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

    public void checkIfAllVmsAreAlive(){
        ArrayList<VM> migrationVms = new ArrayList<>();
        for(PM pm : this.getPms()){
            //for cascading if a PM fails
            if (pm.isAlive() == false) {
                if (pm.isInMigrationProcess() == false) {
                    if (pm.getCapacity() > 0) {
                        pm.setInMigrationProcess(true);
                    }
                }
            } else {
                ArrayList<VM> migrationVmsFromPM = new ArrayList<>();
                migrationVmsFromPM = pm.checkIfAllVmsAreAlive();
                migrationVms.addAll(migrationVmsFromPM);
            }
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
                            VM migrationMapVm = null;
                            try {
                                migrationMapVm = getMigrationMapVm(entry.getKey(), entry.getValue());
                            }catch (NullPointerException e){
                                System.out.println("no free space in PM's");
                                System.exit(1);
                            }
                            int difference = migrationVm.getMemory().countDirtyPages() - migrationMapVm.getMemory().countDirtyPages();
                            if (difference >= 1) {
                                vmig += difference * migrationMapVm.getMemory().getPages().get(0).getSize();
                                System.out.println("second migration: " + migrationVm.toString());
                                doSecondMigration(migrationVm, entry.getValue());
                            } else {
                                System.out.println("third migration: " + migrationVm.toString());
                                doLastMigration(migrationVm, entry.getValue());
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
        migrationMap.put(migrationMapVm.getId().getId(), migrationMapPm);


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

    public long getVmig() {
        return vmig;
    }
}
