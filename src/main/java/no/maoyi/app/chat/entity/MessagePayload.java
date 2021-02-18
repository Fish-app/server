package no.maoyi.app.chat.entity;

public class MessagePayload< T > {
    T payload;
    PayloadType type;

    enum PayloadType{
        IMAGE,
        TEXT
    }
}
