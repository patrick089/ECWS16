package ecws16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;

public class VM {
    private int size;
    private ID id;
    private Memory memory;
    private ArrayList<Request> requests;
    private boolean isAlive;
    private boolean inMigrationProgress;

    public VM(int size) {
        id = new ID();
        this.size = size;
        memory = new Memory(size);
        requests = new ArrayList<Request>();
        isAlive = true;
        inMigrationProgress = false;
        //TODO change
    }
    public VM (VM vm){
        id = new ID(vm.getId());
        this.size = vm.getSize();
        this.memory = new Memory(vm.getMemory());
        requests = new ArrayList<Request>();
        for (int i = 0; i < vm.getRequests().size(); i++){
            requests.add(vm.getRequests().get(i));
        }
        isAlive = vm.isAlive();
        inMigrationProgress = vm.isInMigrationProgress();

    }

    public ArrayList<Request> timeStep(long currentTime) {
        ArrayList<Request> removedRequests = new ArrayList<>();
        for(int i = 0; i < requests.size(); i++){
            if(requests.get(i).isFinished(currentTime)){
                //System.out.println("Removing Request: " + requests.get(i).toString());
                Request request = requests.get(i);
                requests.remove(i--);
                removedRequests.add(request);
            }
        }
        return removedRequests;
    }

    public void handleRequest(Request request, int modus, double failureProbabilty){
        request.setVmId(this.getId().getId());
        boolean failure = false;
        if(modus == 3 || modus == 4){
            failure = Math.random() < failureProbabilty;
            if(failure == true){
                request.setDelivered(false);
            }else {
                request.setDelivered(true);
            }
        }
        if (failure == false) {
            requests.add(request);
            //System.out.println("Adding Request: " + request.toString());
            memory.makePageDirty();
        }

    }

    public Memory getMemory() {
        return memory;
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    public int getFreeCapacity() {
        return memory.freeCapacity();
    }

    public int getCapacity(){ return size - memory.freeCapacity();}

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public void die(){
        setAlive(false);
        setInMigrationProgress(false);
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public boolean isInMigrationProgress() {
        return inMigrationProgress;
    }

    public void setInMigrationProgress(boolean inMigrationProgress) {
        this.inMigrationProgress = inMigrationProgress;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VM vm = (VM) o;

        if (size != vm.size) return false;
        if (isAlive != vm.isAlive) return false;
        if (inMigrationProgress != vm.inMigrationProgress) return false;
        if (id != null ? !id.equals(vm.id) : vm.id != null) return false;
        if (memory != null ? !memory.equals(vm.memory) : vm.memory != null) return false;
        return requests != null ? requests.equals(vm.requests) : vm.requests == null;

    }

    @Override
    public int hashCode() {
        int result = size;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (memory != null ? memory.hashCode() : 0);
        result = 31 * result + (requests != null ? requests.hashCode() : 0);
        result = 31 * result + (isAlive ? 1 : 0);
        result = 31 * result + (inMigrationProgress ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VM{" +
                "size=" + size +
                ", id=" + id +
                ", memory=" + memory +
                ", requests=" + requests +
                ", isAlive=" + isAlive +
                ", inMigrationProgress=" + inMigrationProgress +
                '}';
    }
}
