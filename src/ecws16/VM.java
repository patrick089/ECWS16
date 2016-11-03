package ecws16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;

public class VM {
    /*
    Size, consumed memory, consumed CPU, consumed network bandwidth (de-
pends on the consumed memory), page dirtying rate (depends linearly on
the combination of the utilized memory, CPU and network bandwidth)– Running time given by a normal distribution function
– Origin of the request
• This is the location of the user, who is served by this VM
     */
    //TODO add variables like size, consumed memory, ...
    private int size;
    private int numberOfMaxRequests;
    private int memory;
    private ArrayList<Request> requests;

    public VM() {
        size = 50;
        numberOfMaxRequests = 5;
        memory = 0;
        requests = new ArrayList<Request>();
        //TODO change
    }

    public ArrayList<Request> timeStep(long currentTime) {
        //ArrayList<Request> newRequests = new ArrayList<>();
        //newRequests.addAll(requests);
        ArrayList<Request> removedRequests = new ArrayList<>();
        for(int i = 0; i < requests.size(); i++){
            if(requests.get(i).getTimestamp() < currentTime - requests.get(i).getDuration()){
                System.out.println("Removing Request: " + requests.get(i).toString());
                //newRequests.remove(i);
                Request request = requests.get(i);
                requests.remove(i--);
                removedRequests.add(request);
            }
        }
        //requests = newRequests;
        return removedRequests;
    }

    public void handleRequest(Request request){

        memory += request.getSize();
        requests.add(request);
        System.out.println("Adding Request: " + request.toString());
        //TODO: Dirty pages


    }


    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }
}
