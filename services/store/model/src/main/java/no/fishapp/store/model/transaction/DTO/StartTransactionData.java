package no.fishapp.store.model.transaction.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartTransactionData {

    @NonNull
    int listingId;

    @NotNull
    int amount;

}
