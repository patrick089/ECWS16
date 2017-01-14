package ecws16;

import java.util.ArrayList;

public class PM {
    private static final double U_0 = 100;
    private static final double U_cpu = 100;
    private static final double U_mem = 100;
    private static final double U_network = 100;
    private static final double FAILURE_PROBABILITY = 0.001;
    private static final long restartDuration = 5;

    private ID id;
    private ArrayList<VM> vms;
    private long timeLeftUntilRestarted;
    private boolean isStarted;
    private int size;
    private boolean isAlive;
    private boolean inMigrationProcess;



    public PM(int numberOfVms, int size) {
        this.id = new ID();
        this.vms = new ArrayList<>();
        timeLeftUntilRestarted = 0;
        this.size = size;
        isStarted = true;
        for (int i = 0; i < numberOfVms; i++){
            vms.add(new VM(50));
        }
        isAlive = true;
        inMigrationProcess = false;
    }


    public double getEnergyUtilization() {
        if (!isStarted) {
            return 0;
        }
        double energyUtilization = U_0;
        energyUtilization += U_cpu * getWorkloadCPU();
        energyUtilization += U_mem * getWorkloadMem();
        energyUtilization += U_network * getWorkloadNetwork();
        return energyUtilization;
    }

    public void restart() {
        isStarted = true;
        timeLeftUntilRestarted = restartDuration;
    }

    public void shutdown() {
        isStarted = false;
    }

    public ArrayList<Request> timeStep(long t) {
        ArrayList<Request> removedRequests = new ArrayList<>();
        if (isStarted) {
            if (timeLeftUntilRestarted > 0) {
                timeLeftUntilRestarted--;
            } else {
                if (Math.random() < FAILURE_PROBABILITY) {
                    restart();
                }
                for (int i = 0; i < vms.size(); i++) {
                    VM vm = vms.get(i);
                    removedRequests = vm.timeStep(t);
                }
            }
        }
        return removedRequests;
    }

    public void handleRequest(Request request, int modus, double failureProbability){

        request.setPmId(this.getId().getId());
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
            int maxCapacity = 0;
            VM selectedVM = null;
            for (VM vm : vms) {
                if (vm.isAlive() == true) {
                    maxCapacity = vm.getFreeCapacity();
                    if (maxCapacity > maxFreeCapacity /*&& vm.isAlive() == true*/) {
                        maxFreeCapacity = maxCapacity;
                        selectedVM = vm;
                    }
                }
            }
            try {
                selectedVM.handleRequest(request, modus, failureProbability);
            } catch (NullPointerException e) {
                //catch
            }
        }
    }

    public void die(){
        isAlive = false;
        inMigrationProcess = false;
    }

    public ArrayList<VM> checkIfAllVmsAreAlive(){
        ArrayList<VM> migrationVM = new ArrayList<>();
        for(int i = 0; i < this.getVms().size(); i++){
            if(this.getVms().get(i).isInMigrationProgress() == true){
                migrationVM.add(this.getVms().get(i));
            }
        }
        return migrationVM;
    }


    public int getCapacity() {
        int capacity = 0;
        for(VM vm: vms){
            capacity += vm.getCapacity();
        }
        return capacity;
    }

    public int getFreeCapacity(){
        int freeCapacity = 0;
        for(VM vm : vms){
            freeCapacity += vm.getFreeCapacity();
        }
        return freeCapacity;
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getWorkloadCPU() {
        return getCapacity()*0.1;
    }

    public double getWorkloadMem() {
        return getCapacity()*0.12;
    }

    public double getWorkloadNetwork() {
        return getCapacity()*0.23;
    }

    public ArrayList<VM> getVms() {
        return vms;
    }

    public void setVms(ArrayList<VM> vms) {
        this.vms = vms;
    }

    public boolean isInMigrationProcess() {
        return inMigrationProcess;
    }

    public void setInMigrationProcess(boolean inMigrationProcess) {
        this.inMigrationProcess = inMigrationProcess;
        if(inMigrationProcess == true){
            for(VM vm : this.getVms()){
                vm.setInMigrationProgress(true);
            }
        }
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public ID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "PM{" +
                "id=" + id +
                ", vms=" + vms +
                ", timeLeftUntilRestarted=" + timeLeftUntilRestarted +
                ", isStarted=" + isStarted +
                ", size=" + size +
                ", isAlive=" + isAlive +
                ", inMigrationProcess=" + inMigrationProcess +
                '}';
    }
}
