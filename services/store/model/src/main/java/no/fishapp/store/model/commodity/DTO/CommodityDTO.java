package no.fishapp.store.model.commodity.DTO;

import lombok.Data;
import no.fishapp.store.model.commodity.Commodity;

import javax.validation.constraints.NotNull;

/**
 * DTO used to send {@link Commodity} data.
 */
@Data
public class CommodityDTO extends Commodity {

    @NotNull
    Commodity commodity;

    @NotNull
    double cheapestPrice;

    public CommodityDTO(Commodity commodity, double cheapestPrice) {
        this.commodity = commodity;
        this.cheapestPrice = cheapestPrice;
    }

}
