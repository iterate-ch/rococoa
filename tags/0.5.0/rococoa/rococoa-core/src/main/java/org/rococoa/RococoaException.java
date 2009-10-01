package org.rococoa;

@SuppressWarnings("serial")
public class RococoaException extends RuntimeException {

    public RococoaException() {
    }

    public RococoaException(String message) {
        super(message);
    }

    public RococoaException(Throwable cause) {
        super(cause);
    }

    public RococoaException(String message, Throwable cause) {
        super(message, cause);
    }

}
