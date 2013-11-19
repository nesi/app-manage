package nz.org.nesi.appmanage.exceptions;

/**
 * Project: Applications
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 15/11/13
 * Time: 8:30 AM
 */
public class AppFileException extends RuntimeException {

    public AppFileException(String msg) {
        super(msg);
    }

    public AppFileException(String msg, Exception cause) {
        super(msg, cause);

    }
}
