package no.fishapp.util.exceptionmappers;

import lombok.Getter;

public class InvalidInputDataException extends Exception {

    @Getter
    private final String returnMessage;

    public InvalidInputDataException(String returnMessage) {
        this.returnMessage = returnMessage;
    }
}
