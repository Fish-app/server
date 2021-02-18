package no.maoyi.app.chat.control;

import no.maoyi.app.chat.entity.Conversation;
import no.maoyi.app.chat.entity.Message;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class ChatService {

    @PersistenceContext
    EntityManager em;

    Conversation findConversationById(Long id) {
        // Select all converstations in db
        // Return first result if found, otherwise return null
        return null;
    }

    boolean addMessage(Message message, Conversation conversation) {
        // Find the conversation by id
        // If found, add a message to the List inside the conversation
        return false;
    }

    void attachImageToMessage(Message message) {
        // Fin the message by id, and attach a image to the m
    }


    Message getSingleMessageById(Long id) {
        // Find the message by id with JPA and return message if found
        return null;
    }
}
