/**
 * A class used to communicate errors with operations involving the
 * order of FourTree operations.
 *
 * @author Sean Strout, James Heliotis
 */
public class FourZipException extends Exception {
    /**
     * Create a new FourZipException
     * @param msg the reason this exception object is being thrown
     */
    public FourZipException(String msg) {
        super(msg);
    }
}
