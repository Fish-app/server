package no.fishapp.checkout.model.dibsapi.responses;

import lombok.Data;

import java.util.List;

@Data
public class BulkChargeResponse {

    private List<BulkChargeResponseItem> page;

    private boolean more;

}
