package ecws16;

/**
 * Class for the retryStrategy
 */
public class RetryStrategy {

    public static final int DEFAULT_NUMBER_OF_RETRIES = 5;

    private int numberOfRetries; //total number of tries
    private int numberOfTriesLeft; //number left
    //possible improvement: after number of tries, we waiting a time to try it again
    //private long timeToWait; //wait interval

    public RetryStrategy()
    {
        this(DEFAULT_NUMBER_OF_RETRIES);
    }

    public RetryStrategy(int numberOfRetries)
    {
        this.numberOfRetries = numberOfRetries;
        numberOfTriesLeft = numberOfRetries;
        //this.timeToWait = timeToWait;
    }

    public boolean shouldRetry()
    {
        return numberOfTriesLeft > 0;
    }

    public void errorOccured() throws RetryException{
        numberOfTriesLeft--;

        /*if(!shouldRetry()) {
            throw new RetryException(numberOfRetries +  " attempts to retry failed ");
        }*/
    }

    /*public long getTimeToWait()
    {
        return timeToWait ;
    }*/


}
