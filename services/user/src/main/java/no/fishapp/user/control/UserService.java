package no.fishapp.user.control;


import no.fishapp.user.entity.User;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

public class UserService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    JsonWebToken webToken;


    /**
     * Returns a user from the provided user id
     *
     * @param userId the user id to find the user from
     * @return the user if found null if not
     * @throws NoResultException
     */
    public User getUser(long userId) throws NoResultException {
        try {
            return entityManager.find(User.class, userId);
        } catch (Exception ignored) {
        }
        return null;
    }


    /**
     * Returns the logged in user - if not logged in or error fetching user null is returned
     *
     * @return the logged in user or null
     */
    public User getLoggedInUser() {
        return getUser(Long.parseLong(webToken.getSubject()));
    }

    /**
     * Adds a conversation to the user if not already present, returns the user object
     *
     * @param conversation the conversation to add
     * @return the updated user object with the list of conversations
     */
//    public void addConversationToUser(Conversation conversation, User userToAdd) {
//        // Check if user has the conversation, if true do nothing
//        if (!isUserInConversation(userToAdd, conversation)) {
//            // User does not have the conversation in the list, therefore we add it
//            List<Conversation> conversationList = userToAdd.getUserConversations();
//            conversationList.add(conversation);
//            entityManager.merge(userToAdd);
//            entityManager.flush();
//        }
//    }

    /**
     * Check if the user already has the conversation in the list (to avoid duplicates)
     *
     * @param u       user to query
     * @param cToTest test if the user already has this conversation in the list
     * @return true if the user has the conversation in the list
     */
//    private boolean isUserInConversation(User u, Conversation cToTest) {
//        List<Conversation> conversationList = u.getUserConversations();
//        for (Conversation cInList : conversationList) {
//
//            if (cInList.getId() == cToTest.getId()) {
//                return true;
//            }
//        }
//        return false;
//    }

}
