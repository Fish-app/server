package no.fishapp.store.model.listing.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.store.model.listing.Listing;

/**
 * {@code ChatListingInfo} is a DTO used for communicating with the chat microservice-component
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatListingInfo {

    Boolean isOpen;
    long creatorId;

    public ChatListingInfo(Listing listing) {
        this.isOpen = listing.getIsOpen();
        this.creatorId = listing.getCreatorId();
    }

}
