package no.fishapp.store.model.commodity.DTO;

import lombok.Data;
import no.fishapp.store.model.commodity.Commodity;

import javax.validation.constraints.NotNull;


@Data
public class CommodityDTO extends Commodity {

    @NotNull
    Commodity commodity;

    @NotNull
    double cheapestPrice;

    public CommodityDTO(Commodity commodity) {
        this.commodity = commodity;
//        this.cheapestPrice = commodity.getOfferListings()
//                                      .stream()
//                                      .min((o1, o2) -> (int) (o1.getPrice() - o2.getPrice()))
//                                      .map(Listing::getPrice)
//                                      .orElse(- 1D);
    }

}
