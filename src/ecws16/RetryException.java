package ecws16;

/**
 * Created by Patrick on 08.11.16.
 */
public class RetryException extends Exception {

    public RetryException() {}

    public RetryException(String message)
    {
        super(message);
    }
}
