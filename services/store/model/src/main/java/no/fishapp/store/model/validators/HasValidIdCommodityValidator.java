package no.fishapp.store.model.validators;

import no.fishapp.store.model.commodity.Commodity;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HasValidIdCommodityValidator implements ConstraintValidator<HasValidId, Commodity> {


    @Override
    public boolean isValid(Commodity value, ConstraintValidatorContext context) {
        if (value == null || value.getId() == null) {
            return false;
        } else {
            long id = value.getId();
            return id > 0;
        }
    }
}
