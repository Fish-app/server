package no.fishapp.store.model.listing.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fishapp.store.model.listing.Listing;

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
