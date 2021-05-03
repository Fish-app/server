package no.fishapp.util.multipartHandler;

public class MultipartReadException extends Exception {

    public MultipartReadException(String message, Throwable cause, String paramName) {
        super(message, cause);
        this.paramName = paramName;
    }

    public MultipartReadException(String message, String paramName) {
        super(message);
        this.paramName = paramName;
    }

    public String paramName;
}
