package no.fishapp.app.transaction.entity.DTO;


import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

@Data
public class StartTransactionData {

    @NonNull
    int listingId;

    @NotNull
    int amount;

}
