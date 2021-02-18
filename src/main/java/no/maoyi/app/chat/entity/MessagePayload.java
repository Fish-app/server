package no.maoyi.app.chat.entity;

public interface MessagePayload {
    Generic getMessagePayLoad();
    Boolean setMessagePayload(String payload);

    enum PayloadType{
        IMAGE,
        TEXT
    }
}
