package me.kenzierocks.hnbt;

/**
 * Thrown when the parsing of HNBT fails.
 */
public class HNBTParsingException extends Exception {

    private static final long serialVersionUID = -1260202145611792778L;

    public HNBTParsingException() {
    }

    public HNBTParsingException(String message) {
        super(message);
    }

    public HNBTParsingException(Throwable cause) {
        super(cause);
    }

    public HNBTParsingException(String message, Throwable cause) {
        super(message, cause);
    }

}
