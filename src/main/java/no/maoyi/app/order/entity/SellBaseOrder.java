package no.maoyi.app.order.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;


@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SellBaseOrder extends BaseOrder {


}