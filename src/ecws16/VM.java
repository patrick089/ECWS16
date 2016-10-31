package ecws16;

public class VM {
    /*
    Size, consumed memory, consumed CPU, consumed network bandwidth (de-
pends on the consumed memory), page dirtying rate (depends linearly on
the combination of the utilized memory, CPU and network bandwidth)– Running time given by a normal distribution function
– Origin of the request
• This is the location of the user, who is served by this VM
     */
    //TODO add variables like size, consumed memory, ...
    private Request request;
    private long remainingTimeSteps;
    public VM(Request request) {
        this.request = request;
        remainingTimeSteps = 100; //TODO change
    }

    public void timeStep() {
        remainingTimeSteps--;
    }

    public boolean isFinished() {
        return remainingTimeSteps <= 0;
    }
}
