package no.maoyi.app.conversation.entity;

public interface MessagePayload {
    Generic getMessagePayLoad();
    Boolean setMessagePayload(String payload);

    enum PayloadType{
        IMAGE,
        TEXT
    }
}
